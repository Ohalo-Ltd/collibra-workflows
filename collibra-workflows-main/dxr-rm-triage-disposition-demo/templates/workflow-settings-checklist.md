# Workflow Settings Checklist (Copy/Paste)

Use this checklist when importing into a new Collibra environment.

## Definition

- [ ] Definition uploaded and enabled
- [ ] API v2 namespace present (`http://www.collibra.com/apiv2`)

## Applies To

- [ ] `File` is selected
- [ ] Optional: community/domain scope set for controlled demo
- [ ] Non-required applies-to rows removed

## Start behavior

- [ ] `Start Events` selected (event-trigger model)
- [ ] For single-file demo: `Asset Status Changed` or `Asset Attribute Changed`

## Roles and permissions

- [ ] `Start workflow` includes valid global starter role(s)
- [ ] Demo user has `Start workflow` global permission
- [ ] Demo user has `Participate in workflow` global permission

## Reliability controls

- [ ] `Perform candidate user check` enabled
- [ ] BPMN tasks have valid assignee/candidates
- [ ] `Run once per resource` set according to test strategy

## Validation test

- [ ] Trigger event on one file
- [ ] Task appears in `Tasks -> Your Tasks`
- [ ] Complete one branch to end event
- [ ] Verify file metadata + history evidence
