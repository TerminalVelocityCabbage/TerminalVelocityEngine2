package com.terminalvelocitycabbage.engine.mod;

import com.terminalvelocitycabbage.engine.Entrypoint;

import java.util.jar.JarFile;

public record Mod(Entrypoint entrypoint, JarFile jarFile, ModInfo info) {

}
