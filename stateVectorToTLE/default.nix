with import <nixpkgs> {};

let

  unstable = import <nixos-unstable> {};

in

stdenv.mkDerivation {
  name = "Napoleon";
  buildInputs = [
    maven
  ];
  shellHook = ''
      mvn compile; mvn exec:java -D"exec.mainClass"="msp.simulator.Main"
  '';
}
