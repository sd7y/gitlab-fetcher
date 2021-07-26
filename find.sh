#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# The target dir
ROOT_DIR="./gitlab"

# Key word
FILTER="$1"

# Max search depth
MAX_DEPTH=1

if [[ -z "$FILTER" ]] ; then
    echo "Please input a filter."
    exit 1
fi

function find_git_dir() {
    local parent_dir current_dir depth matched
    parent_dir="$1"
    depth=$2
    if [[ $depth -gt $MAX_DEPTH ]]; then
        return
    fi
    cd "$parent_dir"
    if ls -a | grep ^.git$ > /dev/null; then
        matched="$(git log --oneline 2>/dev/null | grep -ie "^[^ ]* .*$FILTER.*")"
        if [[ -n "$matched" ]] ; then
            echo -e "${YELLOW}-----------------------------------${NC}"
            echo -e "${GREEN}$parent_dir${NC}"
            echo "$matched"
            echo -e "${YELLOW}-----------------------------------${NC}"
            echo ""
        fi
    else
        while read line; do
            current_dir="$parent_dir/$line"
            if [[ -d $current_dir ]]; then
                find_git_dir "$current_dir" $((depth+1))
            fi
        done < <(ls)
    fi
}

find_git_dir "$ROOT_DIR"
