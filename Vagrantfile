# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|
  # Every Vagrant development environment requires a box. You can search for
  # boxes at https://atlas.hashicorp.com/search.
  config.vm.box = "ubuntu/wily64"

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine. In the example below,
  # accessing "localhost:8080" will access port 80 on the guest machine.
  config.vm.network "forwarded_port", guest: 9005, host: 9005
  config.vm.network "forwarded_port", guest: 9006, host: 9006

  config.vm.synced_folder "./", "/topology_source"

  config.vm.provider "virtualbox" do |vb|
     vb.gui = false
     vb.memory = "2048"
  end

  config.vm.provision "shell", inline: <<-SHELL
    sudo apt-get update
    sudo apt-get install -y openjdk-8-jdk openjdk-8-jre openjdk-8-jre-headless maven build-essential
    yes | DD_API_KEY=#{ENV['DD_API_KEY']} bash -c "$(curl -L https://raw.githubusercontent.com/DataDog/dd-agent/master/packaging/datadog-agent/source/install_agent.sh)"
    cp -aR /topology_source /home/vagrant/.
    echo "use_dogstatsd : yes" | sudo tee -a /etc/dd-agent/dogstatsd.conf
    echo "dogstatsd_port : 8125" | sudo tee -a /etc/dd-agent/dogstatsd.conf
    echo "hostname : vagrant-datadog-storm-example" | sudo tee -a /etc/dd-agent/dogstatsd.conf
    service datadog-agent restart
    cd topology_source
    sudo update-ca-certificates -f
    mvn package
    echo "java -cp ~/target/jar/datadog-storm-example-1.0.0.jar storm.starter.ExclamationTopology" > ~/start_topology.sh
    chmod +x ~/start_topology.sh
  SHELL
end
