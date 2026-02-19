import com.collibra.dgc.core.api.dto.activitystream.FindActivitiesRequest
import com.collibra.dgc.core.api.dto.activitystream.ActivityFilterCategory
import com.collibra.dgc.core.api.dto.workflow.StartWorkflowInstancesRequest
import com.collibra.dgc.core.api.model.workflow.WorkflowBusinessItemType
import groovy.json.JsonSlurper

// =====================================================
// CONFIG
// =====================================================
final UUID approvalWorkflowDefinitionId = UUID.fromString('019bf147-fbe4-75bd-8a25-43766b9feb26')

// Data Attribute IDs (assets) created by DXR
final Set<UUID> sensitiveDataAttributeIds = [
    UUID.fromString('019bf792-e3e1-74c4-9297-8f3b1d176a88'),
    UUID.fromString('019bf6ee-a7e9-70b8-9a1c-e4f99f29c474')
] as Set

final int lookbackMinutes = 15
final int maxActivities   = 1000

// =====================================================
// HELPERS
// =====================================================
def prop(def obj, String name) {
    if (obj == null) return null
    try { return obj."$name" } catch (MissingPropertyException ignored) { return null }
}

UUID asUuid(def v) {
    if (v == null) return null
    if (v instanceof UUID) return (UUID) v
    if (v instanceof String) {
        try { return UUID.fromString(v) } catch (Exception ignored) { return null }
    }
    return null
}

List safeResults(def page) {
    def r = null
    try { r = page?.results } catch (ignored) {}
    if (r == null) { try { r = page?.getResults() } catch (ignored) {} }
    if (r == null) { try { r = page?.activities } catch (ignored) {} }
    return (r ?: []) as List
}

def getActivityService() {
    try { return activityStreamApi } catch (MissingPropertyException ignored) {}
    try { return activityApi } catch (MissingPropertyException ignored) {}
    return null
}

def parseDescription(def act) {
    def d = act?.description
    if (!d) return null
    try { return new JsonSlurper().parseText(d) } catch (Exception ignored) { return null }
}

boolean isFileAsset(UUID id) {
    try {
        def a = assetApi.getAsset(id)
        def typeName = (
            prop(a, 'type') != null ? prop(prop(a,'type'),'name') : null
        ) ?: prop(a,'typeName') ?: ''
        return typeName.toString().equalsIgnoreCase('File')
    } catch (Exception ignored) {
        return false
    }
}

boolean approvalAlreadyRunning(UUID fileId) {
    try {
        def clazz = Class.forName('com.collibra.dgc.core.api.dto.workflow.FindWorkflowInstancesRequest')
        def b = clazz.builder()

        if (b.metaClass.respondsTo(b, 'workflowDefinitionId', UUID)) b.workflowDefinitionId(approvalWorkflowDefinitionId)
        if (b.metaClass.respondsTo(b, 'businessItemIds', List))      b.businessItemIds([fileId])
        else if (b.metaClass.respondsTo(b, 'businessItemId', UUID))  b.businessItemId(fileId)

        if (b.metaClass.respondsTo(b, 'limit', Integer)) b.limit(25)

        def req = b.build()

        def page = null
        try { page = workflowInstanceApi.findWorkflowInstances(req) }
        catch (MissingMethodException ignored) {
            try { page = workflowInstanceApi.getWorkflowInstances(req) } catch (MissingMethodException ignored2) {}
        }

        def instances = safeResults(page)

        return instances.any { inst ->
            def status = (prop(inst,'status') ?: prop(inst,'state') ?: '').toString().toUpperCase()
            def end    = prop(inst,'endDate') ?: prop(inst,'endTime')
            return (end == null) || status.contains('RUN') || status.contains('ACTIVE')
        }
    } catch (Throwable ignored) {
        return false
    }
}

// =====================================================
// MAIN
// =====================================================
long now    = new Date().time
long cutoff = now - (lookbackMinutes * 60L * 1000L)

loggerApi.info("DXR Sweeper: run started. Looking for DXR changes since " + new Date(cutoff))

def activitySvc = getActivityService()
if (activitySvc == null) {
    loggerApi.error("DXR Sweeper: activity stream API not available in this workflow context.")
    return
}

def req = FindActivitiesRequest.builder()
    .startDate(cutoff)
    .endDate(now)
    .categories([ActivityFilterCategory.RELATION] as Set)
    .limit(maxActivities)
    .build()

def page = activitySvc.getActivities(req)
def activities = safeResults(page)

def candidateFileIds = [] as Set<UUID>

activities.eachWithIndex { act, idx ->
    def desc = parseDescription(act)

    if (idx < 3) {
        loggerApi.info("DXR Sweeper: desc=" + act?.description)
    }

    if (!desc) return

    def coRole = (desc?.coRole ?: '').toString()
    if (coRole && !coRole.equalsIgnoreCase('represented by')) {
        return
    }

    UUID sourceId = asUuid(desc?.source?.id)
    UUID targetId = asUuid(desc?.target?.id)
    def sourceType = desc?.source?.type
    def targetType = desc?.target?.type

    if (sourceId && sensitiveDataAttributeIds.contains(sourceId) && targetType == 'TE') {
        candidateFileIds << targetId
        return
    }

    if (targetId && sensitiveDataAttributeIds.contains(targetId) && sourceType == 'TE') {
        candidateFileIds << sourceId
        return
    }
}

loggerApi.info("DXR Sweeper: " + activities.size() + " activities, " + candidateFileIds.size() + " candidate Files")

int started = 0
int skippedAlreadyRunning = 0
int skippedNotAFile = 0

candidateFileIds.each { UUID id ->
    try {
        if (!isFileAsset(id)) {
            skippedNotAFile++
            return
        }
        if (approvalAlreadyRunning(id)) {
            skippedAlreadyRunning++
            return
        }

        def startReq = StartWorkflowInstancesRequest.builder()
            .workflowDefinitionId(approvalWorkflowDefinitionId)
            .businessItemType(WorkflowBusinessItemType.ASSET)
            .businessItemIds([id])
            .build()

        workflowInstanceApi.startWorkflowInstances(startReq)
        started++
        loggerApi.info("DXR Sweeper: started approval workflow for File " + id)
    } catch (Exception e) {
        loggerApi.error("DXR Sweeper: error processing candidate " + id, e)
    }
}

loggerApi.info(
    "DXR Sweeper: run finished. started=" + started
    + ", skippedAlreadyRunning=" + skippedAlreadyRunning
    + ", skippedNotAFile=" + skippedNotAFile
)
