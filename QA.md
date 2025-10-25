# QA Checklist

## Manual pairing and journal sharing checks
- Link an expectant parent and partner using the Settings invite flow, then verify the new “Share journal with partner” toggle appears for the expectant parent.
- With sharing turned off, confirm the partner sees the blocked journal message and cannot add entries from the Journal screen.
- Enable the share toggle and ensure the partner immediately gains access to existing entries and the add-entry button.
- Create a new journal entry on each device and verify it appears for the other after syncing.
- Export the journal to PDF from the expectant parent’s device and confirm the generated file contains the latest entries.

## Regression smoke tests
- Run the existing onboarding flow for both roles and confirm Settings toggles behave as expected.
- Validate that weekly reminder scheduling still works after toggling notification settings.
- Spot check navigation between Home, Journal, and Settings from the drawer.
