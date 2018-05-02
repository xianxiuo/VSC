thisdate="$(date "+%Y年%m月%d日%H时%M分%S秒")"
echo $thisdate | pbcopy
echo "$(pbpaste -Prefer text)"