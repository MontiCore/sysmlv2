#!/bin/bash

# Define the file for which you want to list contributors
file_path=$1

# Get total lines count
total_lines=$(wc -l < "$file_path")

# Get contributors and lines changed
contributors=$(git blame --line-porcelain "$file_path" | grep -o '^author .*' | sort -u | cut -d' ' -f2-)
mapfile -t contributors_array <<< "$contributors"

declare -A lines_changed_array

# Loop through contributors
for contributor in "${contributors_array[@]}"; do
    lines_changed=$(git blame "$file_path" | grep "^.*${contributor}" | wc -l)
    lines_changed_array["$contributor"]=$lines_changed
done

# Print contributors and lines changed
echo "Total lines: $total_lines"
echo "Contributors with more than 10% contribution:"

for contributor in "${!lines_changed_array[@]}"; do
    lines_changed=${lines_changed_array[$contributor]}
    contribution=$(echo "scale=2; $lines_changed / $total_lines * 100" | bc)
    if (( $(echo "$contribution > 10" | bc -l) )); then
        echo "Contributor: $contributor, Lines Changed: $lines_changed, Contribution: $contribution%"
    fi
done | sort -nrk 4

exit 0
