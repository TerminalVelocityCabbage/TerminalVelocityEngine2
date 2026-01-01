package com.terminalvelocitycabbage.test.ecs;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.scripting.api.*;
import com.terminalvelocitycabbage.engine.scripting.api.registry.*;
import com.terminalvelocitycabbage.engine.scripting.core.CoreLibrary;
import com.terminalvelocitycabbage.engine.scripting.core.CoreTypes;
import com.terminalvelocitycabbage.engine.scripting.parser.*;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ScriptsTest {

    public final class Game {

        private final String name;

        public Game(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public final class GameStartEvent extends Event {

        public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("game_started");

        private final Game game;

        public GameStartEvent(Game game) {
            super(EVENT);
            this.game = game;
        }

        public Game getGame() {
            return game;
        }
    }

    @Test
    void testHelloWorldScript() {

        ScriptTypeRegistry scriptTypeRegistry = new ScriptTypeRegistry();
        ScriptActionRegistry scriptActionRegistry = new ScriptActionRegistry();
        ScriptPropertyRegistry scriptPropertyRegistry = new ScriptPropertyRegistry();
        ScriptEventRegistry scriptEventRegistry = new ScriptEventRegistry();
        ScriptConstantRegistry scriptConstantRegistry = new ScriptConstantRegistry();

        CoreLibrary.registerAll(
                scriptTypeRegistry,
                scriptActionRegistry,
                scriptPropertyRegistry,
                scriptEventRegistry,
                scriptConstantRegistry
        );

        ScriptType GAME_TYPE = scriptTypeRegistry.register(ScriptType.of(CoreLibrary.CORE_NAMESPACE, "game")).getElement();

        scriptEventRegistry.register(
                ScriptEvent.<GameStartEvent>builder()
                        .id(new Identifier("test", "game_start"))
                        .eventClass(GameStartEvent.class)
                        .exposedValue("game", GAME_TYPE, GameStartEvent::getGame)
                        .doc("Fires when the game has fully started.")
                        .build()
        );

        scriptPropertyRegistry.register(
                new ScriptProperty<>(
                        new Identifier("test", "game.name"),
                        GAME_TYPE,
                        CoreTypes.TEXT,
                        new PropertyAccess.ReadOnly<>(Game::getName),
                        ScriptVisibility.PUBLIC,
                        "The name/path portion of the identifier."
                )
        );

        String scriptText =
                """
                        on game_started:
                            print(game.name)
                        """;

        //TEMP

        List<ScriptInstruction> instructions = List.of(
                // Load event.game → slot 1
                new LoadEventValueInstruction(
                        1,
                        scriptEventRegistry.get(new Identifier("test", "game_start")).exposedValues().get("game")
                ),

                // Load game.name → slot 2
                new LoadPropertyInstruction(
                        1,
                        2,
                        scriptPropertyRegistry.get(new Identifier("test", "game.name"))
                ),

                // print(slot 2)
                new CallActionInstruction(scriptActionRegistry.get(new Identifier(CoreLibrary.CORE_NAMESPACE, "print")), new int[]{2})
        );


        ExecutionContext context = new ExecutionContext(new Object[3]);
        context.setLocal(0, new GameStartEvent(new Game("test name")));

        for (ScriptInstruction instruction : instructions) {
            instruction.execute(context);
        }

    }
}
