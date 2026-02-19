# Demo Runbook and Talking Script

## Demo goal

Show how Collibra operationalizes records governance for DXR-synced files through event-triggered workflow controls.

## Suggested duration

6 to 8 minutes.

## Demo setup before audience joins

- Workflow enabled and configured with one event trigger (`Asset Status Changed` preferred).
- One target file chosen for demo (for example `Targeting_Package_TGT-0142_2016-06-30_CUI.docx`).
- Browser tabs ready:
  - File asset page
  - Tasks page
  - Workflow flow diagram (optional)

## Talk track (verbatim-friendly)

### 1) Context (30 seconds)

"This file came from our DXR pipeline. Metadata is synced, but governance decisions still need a controlled process. This workflow gives us that process with task ownership and auditability."

### 2) Trigger mechanism (30 seconds)

"In this tenant, we use event-triggered workflow start. A controlled file update launches the RM process."

Action:
- Change file status (or configured attribute) and save.

### 3) Show task creation (30 seconds)

"As soon as that change is saved, the RM intake task is created and linked to this specific file."

Action:
- Open `Tasks -> Your Tasks` and open the RM task.

### 4) Intake triage (60 seconds)

"At intake we decide the governance path. For this run, I am choosing `Disposition Review` to show full controls."

Field values:
- `File Asset ID`: file UUID or URL
- `Action Requested`: `Disposition Review`

### 5) Disposition controls (120 seconds)

"Now we enforce legal and records controls before final disposition. If legal hold is active, destructive actions are blocked or deferred."

Action:
- Complete `Disposition Review`
- Complete `Legal Hold Check`
- Complete `Records Officer Approval` task

### 6) Metadata evidence (90 seconds)

"The output of workflow is not just a checkbox. We update the file metadata that supports records policy execution and reporting."

Show fields on file:
- `Retention Tag`
- `Disposition`
- `Hold Status`
- `Security Classification`
- `Record Designation Date`
- Optional `Disposition Eligible Date`

### 7) Auditability close (40 seconds)

"Every step is auditable: who initiated, who approved, and when. That gives us defensible governance evidence for RM decisions."

Action:
- Open File `History` and optionally workflow instance history.

## Branch alternatives to demo

- `Schedule`: shows retention coding workflow quickly
- `Place Hold`: shows legal hold enforcement path
- `No Action`: shows triage completion with no downstream tasks

## Q&A quick answers

- Is this deleting files?
  - "No. This demo governs decisioning in Collibra; physical delete/transfer integration is out of scope for this phase."
