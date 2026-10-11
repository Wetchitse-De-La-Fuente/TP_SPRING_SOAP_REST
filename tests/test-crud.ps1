$ErrorActionPreference = "Stop"

$base = "http://localhost:8080/api/personnes"

Write-Host "Test 1 : POST - creation"
$corpsCreation = @{
    nom = "Diop"
    prenom = "Aminata"
    age = 25
} | ConvertTo-Json

$personne = Invoke-RestMethod -Uri $base -Method Post `
    -ContentType "application/json" -Body $corpsCreation

if (-not $personne.id) {
    throw "Aucun identifiant retourne par SOAP."
}

$id = $personne.id
Write-Host "Personne creee : id=$id"

Write-Host "Test 2 : GET - une personne"
$personne = Invoke-RestMethod -Uri "$base/$id" -Method Get

if ($personne.nom -ne "Diop") {
    throw "La personne lue ne correspond pas a la personne creee."
}

Write-Host "Test 3 : GET - liste des personnes"
$liste = @(Invoke-RestMethod -Uri $base -Method Get)
$personnesTrouvees = @($liste | Where-Object { $_.id -eq $id })

if ($personnesTrouvees.Count -lt 1) {
    throw "La personne creee est absente de la liste."
}

Write-Host "Test 4 : PUT - modification"
$corpsModification = @{
    nom = "Diop"
    prenom = "Awa"
    age = 26
} | ConvertTo-Json

$personne = Invoke-RestMethod -Uri "$base/$id" -Method Put `
    -ContentType "application/json" -Body $corpsModification

if ($personne.prenom -ne "Awa" -or $personne.age -ne 26) {
    throw "La modification n'a pas ete appliquee."
}

Write-Host "Test 5 : DELETE - suppression"
Invoke-RestMethod -Uri "$base/$id" -Method Delete | Out-Null

Write-Host "Test 6 : GET apres suppression - HTTP 404 attendu"
try {
    Invoke-RestMethod -Uri "$base/$id" -Method Get | Out-Null
    throw "Erreur : le GET aurait du renvoyer HTTP 404."
} catch {
    if ($_.Exception.Response.StatusCode.value__ -ne 404) {
        throw
    }
}

Write-Host "SUCCES : CRUD REST -> SOAP -> H2" -ForegroundColor Green
