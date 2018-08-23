lein clean

lein cljsbuild once prod

rsync -avr --delete-after resources/public/ /media/phil/MyData/new_dev_tree/alchemy-islands/ai_bootdown_site/assets/patterning/tutorial_site/
