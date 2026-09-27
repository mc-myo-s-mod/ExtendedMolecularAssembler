$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$assets = Join-Path $PSScriptRoot '../forge-1-20-1/src/main/resources/assets/extendedmolecularassembler'
foreach ($name in 'part/colored_pattern_encoding_terminal_on', 'part/colored_pattern_encoding_terminal_off', 'item/colored_pattern_encoding_terminal') {
    $model = Get-Content -LiteralPath (Join-Path $assets "models/$name.json") -Raw | ConvertFrom-Json
    $faces = @($model.elements | ForEach-Object { $_.faces.north } | Where-Object { $_ })
    $edge = @($faces | Where-Object texture -EQ '#edge')
    if ($edge.Count -ne 1 -or $null -ne $edge[0].tintindex) { throw "Expected one untinted edge: $name" }
    foreach ($layer in 'Bright', 'Medium', 'Dark') {
        $key = if ($name.StartsWith('part/')) { "lights$layer" } else { 'front_' + $layer.ToLower() }
        $suffix = if ($layer -eq 'Dark') { 'edge_dark' } else { $layer.ToLower() }
        $expected = "extendedmolecularassembler:part/extended_pattern_encoding_terminal_$suffix"
        $tint = @{ Bright = 3; Medium = 2; Dark = 1 }[$layer]
        $face = @($faces | Where-Object texture -EQ "#$key")
        if ($model.textures.$key -ne $expected -or $face.Count -ne 1 -or $face[0].tintindex -ne $tint) {
            throw "Incorrect shared layer: $name / $layer"
        }
    }
    foreach ($tier in 'epic', 'legendary') {
        $childName = $name.Replace('colored_', "${tier}_")
        $child = Get-Content -LiteralPath (Join-Path $assets "models/$childName.json") -Raw | ConvertFrom-Json
        if ($child.parent -ne "extendedmolecularassembler:$name" -or
            @($child.textures.PSObject.Properties).Count -ne 1 -or
            $child.textures.edge -ne "extendedmolecularassembler:part/${tier}_pattern_encoding_terminal_edge") {
            throw "Incorrect tier edge mapping: $childName"
        }
    }
}

$dark = [Drawing.Bitmap]::new((Join-Path $assets 'textures/part/extended_pattern_encoding_terminal_dark.png'))
try {
    foreach ($tier in 'epic', 'legendary') {
        $images = @()
        try {
            foreach ($name in 'extended_pattern_encoding_terminal_bright', 'extended_pattern_encoding_terminal_medium',
                'extended_pattern_encoding_terminal_edge_dark', "${tier}_pattern_encoding_terminal_edge") {
                $images += [Drawing.Bitmap]::new((Join-Path $assets "textures/part/$name.png"))
            }
            foreach ($image in @($dark) + $images) {
                if ($image.Width -ne 16 -or $image.Height -ne 16) { throw "Expected 16x16 textures: $tier" }
            }
            for ($y = 0; $y -lt 16; $y++) {
                for ($x = 0; $x -lt 16; $x++) {
                    $visible = @($images | Where-Object { $_.GetPixel($x, $y).A -gt 0 })
                    if ($visible.Count -gt 1) { throw "Overlapping layers: $tier at $x,$y" }
                    $border = $images[2].GetPixel($x, $y).A -gt 0 -or $images[3].GetPixel($x, $y).A -gt 0
                    if ($border -ne ($dark.GetPixel($x, $y).A -gt 0)) { throw "Incomplete border: $tier at $x,$y" }
                }
            }
            Write-Output "Verified $tier on/off/item mappings, tints and non-overlapping texture masks"
        } finally { foreach ($image in $images) { $image.Dispose() } }
    }
} finally { $dark.Dispose() }
