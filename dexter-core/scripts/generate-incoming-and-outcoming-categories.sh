#!/usr/bin/env bash

source scripts/config.sh


echo "extracting spots for categories"
$JAVA $CLI.categories.ExtractCategoryEdgesCLI --input $WIKI_JSON_DUMP --output $TMP
echo "done in $TMP"

echo "generates incoming and outcoming categories for each entity (in $IN_CATEGORY and $OUT_CATEGORY) "
grep "ARTICLE" cut -f 2,3 > $TTMP

awk -F'	' 'BEGIN{current=$1; incoming=""} {if ($1 == current) {incoming=incoming$2" "} else { print current"\t"incoming; current=$1; incoming=$2" "} } END {print current"\t"incoming}' $TTMP > $OUT_CATEGORIES


sort -nk1,1 -nk2,2 $TTMP | awk -F'	' 'BEGIN{current=$2; incoming=""} {if ($2 == current) {incoming=incoming$1" "} else { print current"\t"incoming; current=$2; incoming=$1" "} } END {print current"\t"incoming}' > $IN_CATEGORIES

