$up   = ".\cli-upload\build\install\cli-upload\bin\cli-upload.bat"
$down = ".\cli-download\build\install\cli-download\bin\cli-download.bat"
$repo = "$env:TEMP\demo-repo"
$jar  = "samples\utils.jar"

#Pokrece se sa ./demo.ps1 u Windows PowerShellu
#Ne mora da se brise folder pri ponovnom pokretanju zbog komande Remove-Item

function Step($tekst) { Write-Host "`n>>> $tekst" -ForegroundColor Cyan; Read-Host "Enter" | Out-Null }

Remove-Item $repo, out-nearest, out-newest -Recurse -Force -ErrorAction SilentlyContinue

Step "Inicijalizacija i konfiguracija repozitorijuma"
& $up LOCAL init --path $repo
& $up LOCAL configure samples\pravila.json --path $repo

Step "Upload komponenti: D (dve verzije), T (test), O (opciona), A, Y, X"
& $up LOCAL upload $jar rs:D:1.0 --path $repo
& $up LOCAL upload $jar rs:D:2.0 --path $repo
& $up LOCAL upload $jar rs:T:1.0 --path $repo
& $up LOCAL upload $jar rs:O:1.0 --path $repo
& $up LOCAL upload $jar rs:A:1.0 --deps "rs:D:1.0:compile,rs:T:1.0:test,rs:O:1.0:compile:optional" --path $repo
& $up LOCAL upload $jar rs:Y:1.0 --deps "rs:D:2.0:compile" --path $repo
& $up LOCAL upload $jar rs:X:1.0 --deps "rs:Y:1.0:compile" --path $repo

Step "Pretraga"
& $up LOCAL list --path $repo
& $up LOCAL list rs --path $repo
& $up LOCAL list rs D --path $repo
& $up LOCAL deps rs:A:1.0 --path $repo

Step "Preuzimanje, strategija NEAREST (D 1.0 jer je bliza korenu)"
& $down LOCAL download samples\demo-spisak.txt out-nearest --path $repo --strategy nearest

Step "Preuzimanje, strategija NEWEST (D 2.0 jer je novija)"
& $down LOCAL download samples\demo-spisak.txt out-newest --path $repo --strategy newest