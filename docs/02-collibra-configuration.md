# Collibra Configuration Guide

This guide documents the exact configuration used for the working demo.

## 1) Import workflow definition

Use one of:

- Recommended: `workflows/dxr_rm_triage_disposition_v4_initiator_assigned.bpmn20.xml`
- Alternate: `workflows/dxr_rm_triage_disposition_v4_role_based.bpmn20.xml`

## 2) Workflow overview settings

In `Workflows -> <your definition> -> Overview`:

- `Applies To`: `File` only
- `Start Events`: choose exactly one during testing
  - Recommended for demo: `Asset Status Changed`
  - Safer targeted trigger: `Asset Attribute Changed`
- `Roles -> Start workflow`: include global roles that can start (for example `Sysadmin`, `DataSteward`)
- `Other`:
  - `Any user can start the workflow`: enabled
  - `Perform candidate user check on workflow start`: enabled (works with initiator+fallback BPMN)
  - `Only run once on a specific resource`: optional; disable while testing repeated runs

## 3) File data model checks

Confirm File page has these fields:

- `Retention Tag`
- `Record Schedule Item`
- `Disposition`
- `Hold Status`
- `Hold Matter ID`
- `Essential Records Priority`
- `Record Designation Date`
- `Security Classification`
- Optional: `Disposition Eligible Date`

## 4) Permissions checks

Verify your user has:

- Global permission: `Start workflow`
- Global permission: `Participate in workflow`
- Rights to update target File attributes during demo

## 5) Triggering patterns

### Pattern A: single-file demo trigger (recommended)

1. Keep workflow enabled with `Asset Status Changed`.
2. Open one target File.
3. Change status (for example `Candidate -> Accepted`) and save.
4. Confirm task appears in `Tasks -> Your Tasks`.

### Pattern B: attribute-based trigger

1. Set `Start Events = Asset Attribute Changed`.
2. Update a low-impact field (for example `Note`) on one file.
3. Confirm workflow starts.


## 6) Reference documentation

- Workflow definition settings:
  - https://productresources.collibra.com/docs/cpsh/latest/Content/Workflows/ManageWorkflows/co_general-wf-settings.htm
- Workflow permissions:
  - https://productresources.collibra.com/docs/collibra/latest/Content/Workflows/co_workflow-permissions.htm
- Start workflow from asset page:
  - https://productresources.collibra.com/docs/collibra/latest/Content/Workflows/ta_start-wf-from-asset-page.htm
