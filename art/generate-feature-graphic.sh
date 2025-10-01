#!/bin/bash

echo "Generating feature graphics to ~/xodos-icons/xodos-feature-graphic.png..."
mkdir -p ~/xodos-icons/
rsvg-convert feature-graphic.svg > ~/xodos-icons/feature-graphic.png
