#!/usr/bin/env bash

set -eu

if [ ! -z "${MODELS}" ]; then
  echo "Models list configured: $MODELS"

  /bin/ollama serve & sleep 5 ;

  IFS=',' read -r -a MODEL_LIST <<< "$MODELS"
  for model in "${MODEL_LIST[@]}"
  do
    echo "Pulling $model ..."
    for i in {1..3}; do
      echo "$model pull try: n°$i ..."
      /bin/ollama pull "$model" && break || sleep 1
    done
  done

  echo "kill 'ollama serve' process for pulling" ;
  ps -ef | grep 'ollama serve' | grep -v grep | awk '{print $2}' | xargs -r kill -9

else
    echo "No models configured."
fi

/bin/ollama
