# Collibra RM Workflow Demo

This repository packages a **Collibra workflow based records management (RM) demo** for unstructured files synced from Data X-Ray (DXR).


## What this demo shows

- RM triage on a File asset: `Schedule`, `Place Hold`, `Disposition Review`, `No Action`
- Routing to Legal and Records Officer decision points
- Human-in-the-loop governance with full auditability in Collibra tasks/history
- Metadata-backed RM outcomes on the File asset (Retention Tag, Hold Status, Disposition, dates, etc.)

## Repository structure

```text
.
├── README.md
├── demo_dataset/
│   └── sample_files_for_demo.csv
├── docs/
│   ├── 01-use-case-overview.md
│   ├── 02-collibra-configuration.md
│   ├── 03-demo-runbook.md
│   ├── 04-troubleshooting.md
│   ├── 05-workflow-variants.md
│   └── 06-publish-to-github.md
├── templates/
│   └── workflow-settings-checklist.md
└── workflows/
    ├── dxr_rm_triage_disposition_v4_initiator_assigned.bpmn20.xml
    └── dxr_rm_triage_disposition_v4_role_based.bpmn20.xml
```

## Workflow variants

### 1) Initiator Assigned 

File: `workflows/dxr_rm_triage_disposition_v4_initiator_assigned.bpmn20.xml`

- Process ID: `dxr_rm_triage_disposition_v4_initiator`
- Start event captures initiator variable
- User tasks are assigned to `${initiator}`
- Fallback candidates are set to `role(DataSteward)` so event starts do not fail with "no candidate users"

Use this when:
- You are triggering from `Asset Status Changed` or `Asset Attribute Changed`

### 2) Role Based

File: `workflows/dxr_rm_triage_disposition_v4_role_based.bpmn20.xml`

- Process ID: `dxr_rm_triage_disposition_v4_role_based`
- User tasks are candidate-assigned via role expression

Use this when:
- Your tenant cleanly resolves resource roles in workflow tasks

## Required data model (File asset)

Ensure these attributes exist and are visible on File pages:

- `Retention Tag`
- `Record Schedule Item`
- `Disposition`
- `Hold Status`
- `Hold Matter ID`
- `Essential Records Priority`
- `Record Designation Date`
- `Security Classification`
- Optional: `Disposition Eligible Date`

## Quick start

1. Import `workflows/dxr_rm_triage_disposition_v4_initiator_assigned.bpmn20.xml`.
2. In workflow definition settings:
   - `Applies To`: `File` only
   - `Start Events`: choose one event trigger (`Asset Status Changed` for easiest test)
   - `Roles > Start workflow`: `Sysadmin` (and any other global role with start permission)
   - `Other`: keep `Any user can start` enabled
3. Enable workflow.
4. Open one File asset and make the triggering change.
5. Complete tasks from `Tasks -> Your Tasks`.

For full setup details, see:
- `docs/02-collibra-configuration.md`
- `docs/04-troubleshooting.md`
- `docs/06-publish-to-github.md`

## Demo flow summary

1. Trigger workflow from a controlled File update.
2. Complete `RM Intake` and choose `Action Requested`.
3. Follow branch tasks:
   - `Schedule` -> schedule and retention update
   - `Place Hold` -> legal hold update
   - `Disposition Review` -> decision + approval path
4. Show final metadata on File and audit trail in History/Tasks.

Detailed presenter script:
- `docs/03-demo-runbook.md`


## Notes

- BPMN namespace is API v2 compatible (`http://www.collibra.com/apiv2`).
- This demo does not perform destructive actions against source storage systems.
- Governance is represented via workflow decisions and Collibra metadata updates.

## References

- Collibra workflow settings docs
- Collibra workflow permissions docs
- Collibra "start workflow from asset" docs

(Links are listed in `docs/02-collibra-configuration.md`.)

## License

This project is licensed under the MIT License. 

## Credits

Original material by Tej Tenmattam (Version 01, February 19, 2026).
