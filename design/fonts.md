# Fonts

The game uses Noto Sans, under the SIL Open Font License 1.1. The licence ships in the app as
`app/src/main/assets/licenses/noto_sans_OFL.txt`. It replaces Segoe UI, whose licence didn't
allow bundling it.

| File | Weight | Used for |
|---|---|---|
| `app/src/main/res/font/noto_sans_black.ttf` | Black (900) | Titles, the high score and the in-game score |
| `app/src/main/res/font/noto_sans_regular.ttf` | Regular (400) | Body text and labels |

## Where they come from

Both are cut from the variable font in the google/fonts repository,
`ofl/notosans/NotoSans[wdth,wght].ttf`, at commit
2984c575fdce412ee02b2baaba67672b9a9434d8 (Noto Sans version 2.015).

Android supports variable font axes only from API 26, and the game supports API 21, so the
two weights are separate static fonts. They keep only Google Fonts' Latin subset, which brings
each file from about 650 KB to 47 KB. Noto Sans has no Reserved Font Name, so the subset keeps
its name.

## Making them again

With fontTools (`pip install fonttools`):

```bash
fonttools varLib.instancer 'NotoSans[wdth,wght].ttf' wght=900 wdth=100 --update-name-table -o black-full.ttf
fonttools varLib.instancer 'NotoSans[wdth,wght].ttf' wght=400 wdth=100 --update-name-table -o regular-full.ttf
LATIN='U+0000-00FF,U+0131,U+0152-0153,U+02BB-02BC,U+02C6,U+02DA,U+02DC,U+0304,U+0308,U+0329,U+2000-206F,U+20AC,U+2122,U+2191,U+2193,U+2212,U+2215,U+FEFF,U+FFFD'
pyftsubset black-full.ttf --unicodes="$LATIN" --layout-features='*' --name-IDs='*' --name-languages='*' --output-file=noto_sans_black.ttf
pyftsubset regular-full.ttf --unicodes="$LATIN" --layout-features='*' --name-IDs='*' --name-languages='*' --output-file=noto_sans_regular.ttf
```
