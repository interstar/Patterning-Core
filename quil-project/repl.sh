echo "\nTo use the live-coding environment, follow these steps:"
echo "1. In Emacs, run: M-x cider-connect (host: localhost, port: 46657)"
echo "2. In the REPL, run: (require 'patterning_quil.dev :reload)"
echo "3. Edit patterns in examples.clj or dev-setup/dev-draw in dev.clj for live coding."
echo "4. Use (patterning_quil.dev/refresh-all) to reload everything."
echo "5. To stop, use (patterning_quil.dev/stop-sketch) or close the Quil window."
lein with-profile dev repl
