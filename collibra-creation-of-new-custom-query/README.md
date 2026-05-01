# Collibra: Create Custom Queries in Data X-Ray

A Collibra workflow that lets a user assemble a Data X-Ray (DXR) query from Collibra assets — Annotators, Extractors, Labels, and Data Sources — and persists the resulting query (and its matching files) back to Collibra as a Query asset.

Originally authored by Vasiliki Nikolopoulou.

## What this workflow does

1. The user starts the process and fills out a form picking any combination of Annotators, Extractors, and Labels (defined as Collibra assets).
2. A Groovy script task:
   - Resolves the picked asset IDs to their human-readable names.
   - Builds a Lucene-style query string (e.g. `labels.name:"PII" AND annotators.name:"Confidential"`).
   - Creates a new Query asset in Collibra with the query as a description attribute.
   - Calls the DXR `GET /api/v1/files?q=...` endpoint with that query.
   - Stores up to 20 matching `(filename, path)` pairs back on the Query asset and surfaces them on a results form.
3. A final user task displays the file count and list to the initiator.

## Contents

```text
.
├── README.md
├── CollibraDataxRayIntegration.mp4   # Demo recording
└── workflow/
    ├── ohalo.app                     # Collibra App definition (JSON) — packages the process + forms
    ├── ohaloprocess.bpmn             # BPMN 2.0 process definition
    ├── form-labelCreation.form       # Start form: pick annotators / extractors / labels / data source
    └── form-hTTPResponse.form        # Results form: shows matched files
```

## Prerequisites

- A Collibra tenant with workflow design enabled.
- A reachable Data X-Ray instance and a bearer token with permission to call `/api/v1/files`.
- The following asset types and domains already exist in Collibra (the form references them by UUID — see `form-labelCreation.form`):
  - Annotator, Extractor, Label, Data Source asset types.
  - A "Query" asset type and a domain to host the created Query assets.

## Required process variables

The process is parameterised entirely via start-event form properties. **No secrets are committed in the BPMN** — the auth token must be supplied at deploy or start time.

| Variable             | Required | Default in source                              | Notes                                                              |
| -------------------- | -------- | ---------------------------------------------- | ------------------------------------------------------------------ |
| `authToken`          | yes      | _(none)_                                       | DXR bearer token. Provide via Collibra deployment, never hardcode. |
| `apiBaseUrl`         | yes      | `https://collibra-demo-integration.dataxray.io` | DXR base URL. Override per environment.                            |
| `requestPath`        | yes      | `/api/v1/files?q=`                             | DXR query endpoint path; the script appends a URL-encoded query.   |
| `connectionname`     | yes      | `ohalo_noauth`                                 | Reserved for the Flowable HTTP task stub (see Roadmap).            |
| `query_asset_type_id`| yes      | `019dcf97-3bac-72c3-8b59-b6ddbe8a8396`         | Asset type UUID for the created Query asset. Tenant-specific.      |
| `queryDomain`        | yes      | `019dcf96-233a-72e1-bf25-8398b8c9146e`         | Domain UUID where new Query assets are created. Tenant-specific.   |

> Update the asset-type / domain UUIDs to match your tenant before deploying.

## Quick start

1. **Open the project in Flowable Design / Collibra Workflow Designer.** Import `workflow/ohalo.app` (it bundles the BPMN and both forms).
2. **Update tenant-specific UUIDs** on the start event in `ohaloprocess.bpmn` (`query_asset_type_id`, `queryDomain`), and check the asset-type / community / domain IDs referenced inside `form-labelCreation.form`.
3. **Provide the DXR auth token.** Two options:
   - Set `authToken` as a process variable when starting the workflow (e.g. via the Collibra UI / API).
   - Replace the inline `HttpClient` call with the Flowable HTTP task stub (`httpTask1`) and store the credential on the `ohalo_noauth` HTTP connection — see Roadmap.
4. **Override `apiBaseUrl`** if you are not pointing at the demo environment.
5. **Deploy** the workflow in Collibra:
   - `Applies To`: appropriate scope (typically `Query`).
   - Make sure the starting user is in the `flowableUser` group (see `flowable:candidateStarterGroups` on the process).
6. **Run** it from Collibra, fill out the start form, and watch the Query asset appear in the chosen domain with its description and matching file paths attached.

## Roadmap / known issues

- The script task currently does its own `HttpClient` call; the BPMN also contains a `httpTask1` Flowable HTTP service task that is **wired but disconnected** (no incoming sequence flow). It is intentionally kept as a stub: long-term the inline call should be replaced by the HTTP task using the `${connectionname}` connection so the auth token never travels through process variables. See the `<documentation>` element on `httpTask1` for activation steps.
- Result list is capped at 20 files (magic number in the script).
- The HTML rendering of the results list and the way the file list is stored as an attribute both need polish.
- Lucene query values are concatenated as strings; values containing `"` would produce malformed queries.

## License

MIT (matches the sibling packages in this repo).
