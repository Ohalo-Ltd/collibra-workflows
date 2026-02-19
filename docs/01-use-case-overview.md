# Use Case Overview: DXR File Records Governance in Collibra

## Problem statement

DXR sends file-centric metadata into Collibra, but RM decisions still need governance workflows:

- Which files should be scheduled for retention?
- Which files must be put on legal hold?
- Which files are disposition candidates?

This workflow provides a repeatable control path with approvals and traceability.

## Business objective

Demonstrate an RM operating model where:

- Trigger: a file event (status or attribute change)
- Triage: business steward selects RM action
- Control checks: legal hold and records approvals are enforced
- Evidence: decisions are captured in tasks and reflected in file metadata

## Primary actors

- Business Steward: triage request
- Legal: hold confirmation/check
- Records Officer / Records Manager: schedule/disposition decisions and approvals

## End-to-end flow

```mermaid
flowchart LR
    A["File Event Trigger"] --> B["RM Intake"]
    B --> C{"Action Requested"}
    C -->|"Schedule"| D["Assign Schedule and Retention"]
    C -->|"Place Hold"| E["Apply Confirm Hold"]
    C -->|"Disposition Review"| F["Disposition Review"]
    C -->|"No Action"| Z["End"]

    F --> G{"Disposition Decision"}
    G -->|"Destroy"| H["Legal Hold Check"]
    H --> I["Records Officer Approval Destroy"]
    G -->|"Transfer"| J["Records Officer Approve Transfer"]
    G -->|"Extend Retention"| K["Records Officer Update Retention"]
    G -->|"No Action"| Z

    D --> Z1["End: Scheduled Tagged"]
    E --> Z2["End: Hold Applied"]
    I --> Z3["End: Approved for Destruction"]
    J --> Z4["End: Approved for Transfer"]
    K --> Z5["End: Retention Updated"]
```

## Scope

In scope:

- Collibra workflow and tasks
- RM metadata update on File asset
- Governance evidence and audit trail

Out of scope:

- Physical delete/transfer in storage platforms
- Fully automated classification-to-retention mapping

## Success criteria

- Workflow reliably starts from configured event
- Tasks route through expected branch
- File metadata reflects decisions
- History and task trail are auditable
