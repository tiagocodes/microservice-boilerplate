
    FROM ubuntu

    RUN ln -fs /usr/share/zoneinfo/Europe/Madrid /etc/localtime

    RUN apt-get update && apt-get install -y telnet lsof vim-nox tzdata iputils-ping net-tools curl wget

    RUN dpkg-reconfigure -f noninteractive tzdata
