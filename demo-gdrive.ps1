$up     = ".\cli-upload\build\install\cli-upload\bin\cli-upload.bat"
$down   = ".\cli-download\build\install\cli-download\bin\cli-download.bat"
$jar    = "samples\utils.jar"

# Podesi putanju do tvog Google OAuth JSON fajla i naziv foldera na Drive-u
$creds  = "config\credentials.json"
$folder = "SKProjekat1-DemoRepo"
$tokens = "$env:TEMP\gdrive-tokens"

function Step($tekst) { Write-Host "`n>>> $tekst" -ForegroundColor Cyan; Read-Host "Enter" | Out-Null }

Remove-Item out-gdrive-nearest, out-gdrive-newest -Recurse -Force -ErrorAction SilentlyContinue

Step "Inicijalizacija i konfiguracija Google Drive repozitorijuma (otvorice se browser za prijavu)"
& $up GDRIVE init --folder $folder --credentials $creds --tokens $tokens
& $up GDRIVE configure samples\pravila.json --folder $folder --credentials $creds --tokens $tokens

Step "Upload komponenti na Google Drive: D (dve verzije), T (test), O (opciona), A, Y, X"
& $up GDRIVE upload $jar rs:D:1.0 --folder $folder --credentials $creds --tokens $tokens
& $up GDRIVE upload $jar rs:D:2.0 --folder $folder --credentials $creds --tokens $tokens
& $up GDRIVE upload $jar rs:T:1.0 --folder $folder --credentials $creds --tokens $tokens
& $up GDRIVE upload $jar rs:O:1.0 --folder $folder --credentials $creds --tokens $tokens
& $up GDRIVE upload $jar rs:A:1.0 --deps "rs:D:1.0:compile,rs:T:1.0:test,rs:O:1.0:compile:optional" --folder $folder --credentials $creds --tokens $tokens
& $up GDRIVE upload $jar rs:Y:1.0 --deps "rs:D:2.0:compile" --folder $folder --credentials $creds --tokens $tokens
& $up GDRIVE upload $jar rs:X:1.0 --deps "rs:Y:1.0:compile" --folder $folder --credentials $creds --tokens $tokens

Step "Pretraga na Google Drive-u"
& $up GDRIVE list --folder $folder --credentials $creds --tokens $tokens
& $up GDRIVE list rs --folder $folder --credentials $creds --tokens $tokens
& $up GDRIVE list rs D --folder $folder --credentials $creds --tokens $tokens
& $up GDRIVE deps rs:A:1.0 --folder $folder --credentials $creds --tokens $tokens

Step "Preuzimanje sa Drive-a, strategija NEAREST (D 1.0 jer je bliza korenu)"
& $down GDRIVE download samples\demo-spisak.txt out-gdrive-nearest --folder $folder --credentials $creds --tokens $tokens --strategy nearest

Step "Preuzimanje sa Drive-a, strategija NEWEST (D 2.0 jer je novija)"
& $down GDRIVE download samples\demo-spisak.txt out-gdrive-newest --folder $folder --credentials $creds --tokens $tokens --strategy newest