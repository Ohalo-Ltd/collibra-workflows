# Troubleshooting Guide

## Issue: Workflow not visible in Actions or Pin Workflows

Symptoms:
- Only built-in approval workflows are visible.
- Custom RM workflow does not show.

Checks:
1. Confirm workflow is `Enabled`.
2. Confirm `Applies To` includes `File`.
3. Confirm user has `Start workflow` and `Participate in workflow` global permissions.
4. Hard refresh UI and re-open file page.

Workaround:
- Use event-trigger (`Asset Status Changed` or `Asset Attribute Changed`) for deterministic start.

## Issue: "No candidate users are defined" on task start

Typical cause:
- Workflow started from event and `${initiator}` is empty, with no fallback candidates.

Fix:
- Use initiator BPMN variant in this repo:
  - `workflows/dxr_rm_triage_disposition_v4_initiator_assigned.bpmn20.xml`
- Ensure tasks include fallback candidates (`role(DataSteward)`).

## Issue: Workflow starts too often

Cause:
- Start event is too broad (for example any attribute change) and sync jobs update many assets.

Fix:
1. Restrict scope with `In community/domain` in `Applies To`.
2. Use a narrower event during demo.
3. Temporarily enable `run once per resource` for protection.

## Issue: Task not assigned to expected person

Checks:
1. If using initiator variant, ensure workflow was user-triggered (not system account) if you rely on initiator assignment.
2. Verify fallback candidate role members exist (for example `DataSteward`).
3. Check task claim behavior in your tenant.

## Issue: Branching does not route correctly

Cause:
- Form values not matching condition expression values.

Expected values:
- `actionRequested`: `schedule`, `place_hold`, `disposition_review`, `no_action`
- `dispositionDecision`: `destroy`, `transfer`, `extend_retention`, `no_action`

## Issue: API v1 warning

Fix:
- Ensure BPMN uses API v2 namespace:
  - `targetNamespace="http://www.collibra.com/apiv2"`
