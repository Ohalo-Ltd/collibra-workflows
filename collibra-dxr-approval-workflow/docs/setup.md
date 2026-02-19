# Collibra Setup Guide

This guide shows how to install and run the DXR Approval Sweeper in Collibra.

## 1. Create / Locate the Approval Workflow

1. In Collibra, create a **Simple Approval Process** workflow or reuse an existing one.
2. Copy the workflow **definition ID** and set it in `workflow/dxr_sweeper.groovy`:

```
final UUID approvalWorkflowDefinitionId = UUID.fromString('...')
```

## 2. Identify Sensitive Data Attributes

Collect the **asset IDs** of the Data Attributes created by DXR. Add them here:

```
final Set<UUID> sensitiveDataAttributeIds = [
    UUID.fromString('...'),
    UUID.fromString('...')
] as Set
```

## 3. Create the Scheduled Workflow (Sweeper)

1. Create a new workflow in Collibra (Timer-driven).
2. Add a **Script Task**.
3. Paste the full contents of `workflow/dxr_sweeper.groovy` into the script task.
4. Set the Timer to run every 5–15 minutes (example: every 5 minutes).

Tip: Use a lookback larger than the timer interval to avoid missing events.

## 4. Confirm Permissions

Ensure the workflow runs as a user with:
- access to Activity Stream API
- permission to read assets and relations
- permission to start the approval workflow

## 5. Test

1. Add a "represented by" relation between one of the sensitive Data Attributes and a File asset.
2. Wait for the timer to run.
3. Confirm that the approval workflow starts for the File.

## Troubleshooting

- If no candidate IDs are found, check the `desc=` log lines to confirm the activity JSON format.
- If approvals do not start, confirm the workflow definition ID is correct and enabled.
- If duplicates appear, increase the guard inside `approvalAlreadyRunning` or add a cooldown.
