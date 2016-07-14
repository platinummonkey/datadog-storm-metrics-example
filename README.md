Apache Storm Datadog Metrics Integration Example
================================================

This shows a quick example of using a statsd metrics consumer to forward metrics to datadog.

Simply `DD_API_KEY=<your datadog api key> vagrant up` then `vagrant ssh` and `./start_topology.sh`

This will need a couple minutes to run, but in <5 minutes you should be able to see your metrics in datadog!

