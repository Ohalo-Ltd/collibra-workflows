# Collibra DXR Approval Workflow

This repository contains a Groovy script and setup guidance for running a Collibra workflow that **automatically starts a simple approval process** when a "represented by" relation is created between a **DXR Data Attribute** and a **File asset**.

It is designed for:
- Collibra workflows (Flowable/Groovy Script task)
- Activity Stream monitoring
- Auto-starting a workflow when sensitive Data Attributes are linked to files

## Contents

- `workflow/dxr_sweeper.groovy` — main Groovy script
- `docs/setup.md` — Collibra setup steps
- `LICENSE` — MIT License

## How It Works

The script:
1. Queries the Activity Stream for recent relation changes
2. Parses each activity JSON `description`
3. Detects `represented by` relations where the **source** is a known sensitive Data Attribute
4. Uses the **target File asset** as the workflow business item
5. Starts the approval workflow if one is not already running

## Quick Start

1. Update the **Data Attribute IDs** in `workflow/dxr_sweeper.groovy`
2. Update the **approval workflow definition ID**
3. Add the script to a **Script Task** inside a scheduled workflow
4. Run on a timer (e.g., every 5 minutes)

## Author

Tej Tenmattam

## License

MIT
