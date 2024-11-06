FROM mcr.microsoft.com/devcontainers/base:ubuntu

#RUN apt-get update && apt-get install -y wget tar perl texlive
RUN cd /tmp && wget https://mirror.ctan.org/systems/texlive/tlnet/install-tl-unx.tar.gz && tar -xf install-tl-unx.tar.gz && cd install-tl-* && perl ./install-tl --no-interaction