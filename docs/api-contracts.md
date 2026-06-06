# API Contracts

## Transaction Service

### POST `/api/transactions`

```json
{
  "customerId": "11111111-1111-1111-1111-111111111111",
  "sourceAccountId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
  "destinationAccountId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
  "amount": 15000.00,
  "currency": "EUR",
  "transactionType": "TRANSFER",
  "channel": "SWIFT",
  "originCountry": "FR",
  "destinationCountry": "AE",
  "reference": "Large transfer",
  "executedAt": "2026-06-05T12:00:00Z"
}
```

## Alert Service

### GET `/api/alerts`

Returns paginated alerts.

### GET `/api/cases`

Returns paginated investigation cases.

### POST `/api/cases/{id}/notes`

```json
{
  "authorId": "22222222-2222-2222-2222-222222222222",
  "content": "Customer profile needs enhanced due diligence."
}
```

## AI Service

### POST `/api/ai/explain`

```json
{
  "caseId": "case-uuid"
}
```
