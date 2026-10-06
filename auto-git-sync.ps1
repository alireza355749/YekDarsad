Set-Location "C:\Users\Alireza\AndroidStudioProjects\YekDarsad"

while ($true) {

    git add .

    $changes = git status --porcelain

    if ($changes) {
        git commit -m "Auto sync $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
        git push
    }

    Start-Sleep -Seconds 300
}