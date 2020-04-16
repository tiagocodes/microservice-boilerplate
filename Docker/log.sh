#!/bin/bash

tail -f logs/`\ls -r logs/ | head -1`/*
