# seed-many-transactions.ps1

$ErrorActionPreference = "Stop"

$ApiBase = "http://localhost:8080/api"
$Count = 500

$customerIdsRaw = docker exec amlgraph-postgres psql -U amlgraph -d amlgraph -At -c "select id from customers.customers;"
$customerIds = $customerIdsRaw -split "`n" | ForEach-Object { $_.Trim() } | Where-Object { $_ -ne "" }

$normalCountries = @("FR", "MA", "DE", "ES", "IT", "BE", "NL")
$highRiskCountries = @("AE", "IR", "KP", "MM")
$types = @("TRANSFER", "PAYMENT", "WITHDRAWAL", "DEPOSIT")
$channels = @("ONLINE", "SWIFT", "ATM", "BRANCH")

Write-Host "Seeding $Count transactions using $($customerIds.Count) customers..."

for ($i = 1; $i -le $Count; $i++) {
    $customerId = $customerIds[(Get-Random -Minimum 0 -Maximum $customerIds.Count)]
    $isSuspicious = (Get-Random -Minimum 1 -Maximum 100) -le 35

    if ($isSuspicious) {
        $amount = Get-Random -Minimum 11000 -Maximum 85000
        $origin = $normalCountries[(Get-Random -Minimum 0 -Maximum $normalCountries.Count)]
        $destination = $highRiskCountries[(Get-Random -Minimum 0 -Maximum $highRiskCountries.Count)]
        $executedAt = [DateTime]::UtcNow.Date.AddDays(-1 * (Get-Random -Minimum 0 -Maximum 15)).AddHours((Get-Random -Minimum 2 -Maximum 6)).AddMinutes((Get-Random -Minimum 0 -Maximum 59))
    } else {
        $amount = Get-Random -Minimum 10 -Maximum 4900
        $origin = $normalCountries[(Get-Random -Minimum 0 -Maximum $normalCountries.Count)]
        $destination = $normalCountries[(Get-Random -Minimum 0 -Maximum $normalCountries.Count)]
        $executedAt = [DateTime]::UtcNow.AddMinutes(-1 * (Get-Random -Minimum 0 -Maximum 50000))
    }

    $body = @{
        customerId = $customerId
        sourceAccountId = [guid]::NewGuid().ToString()
        destinationAccountId = [guid]::NewGuid().ToString()
        amount = $amount
        currency = "EUR"
        transactionType = $types[(Get-Random -Minimum 0 -Maximum $types.Count)]
        channel = $channels[(Get-Random -Minimum 0 -Maximum $channels.Count)]
        originCountry = $origin
        destinationCountry = $destination
        reference = "bulk-demo-transaction-$i"
        executedAt = $executedAt.ToString("yyyy-MM-dd'T'HH:mm:ss'Z'")
    } | ConvertTo-Json

    Invoke-RestMethod `
        -Uri "$ApiBase/transactions" `
        -Method Post `
        -ContentType "application/json" `
        -Body $body | Out-Null

    if ($i % 50 -eq 0) {
        Write-Host "Inserted $i transactions..."
    }
}

Write-Host "Waiting for Kafka pipeline..."
Start-Sleep -Seconds 10

$tx = Invoke-RestMethod "$ApiBase/transactions?size=1"
$alerts = Invoke-RestMethod "$ApiBase/alerts?size=1"
$cases = Invoke-RestMethod "$ApiBase/cases?size=1"
$customers = Invoke-RestMethod "$ApiBase/customers?size=1"

Write-Host "Transactions: $($tx.pagination.totalElements)"
Write-Host "Alerts:       $($alerts.pagination.totalElements)"
Write-Host "Cases:        $($cases.pagination.totalElements)"
Write-Host "Customers:    $($customers.pagination.totalElements)"