#!/bin/bash
ps -ef | grep "hsgene_sync_tool.jar" | grep -v grep | awk '{print $2}' | xargs kill -9