#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 <<-EOSQL
    CREATE DATABASE tech1_iam_server;
EOSQL
