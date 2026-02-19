# Workflow Variants in This Repository

## Overview

Two BPMN variants are provided because tenant behavior varies for manual start visibility and role resolution.

## Variant A: Role Based

File:
- `workflows/dxr_rm_triage_disposition_v4_role_based.bpmn20.xml`

Characteristics:
- Process ID: `dxr_rm_triage_disposition_v4_role_based`
- Task assignment uses role expression candidates.

Best for:
- Tenants where resource-role resolution in tasks is stable.
- Teams that want role-owned queues.

## Variant B: Initiator Assigned (with fallback candidates)

File:
- `workflows/dxr_rm_triage_disposition_v4_initiator_assigned.bpmn20.xml`

Characteristics:
- Process ID: `dxr_rm_triage_disposition_v4_initiator`
- Start event captures initiator variable.
- Each user task has:
  - `flowable:assignee="${initiator}"`
  - fallback `flowable:candidateUsers="{role(DataSteward)}"`

Best for:
- Event-triggered starts.
- Environments where manual start visibility is inconsistent.

## Selection guidance

Use `Initiator Assigned` if any of these are true:
- Workflow only starts reliably from events.
- You saw "no candidate users" errors.
- You need a predictable single-user demo path.

Use `Role Based` if:
- You are running production-like team queues.
- Role-based work distribution is required.
