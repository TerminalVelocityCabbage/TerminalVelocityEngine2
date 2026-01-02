package com.terminalvelocitycabbage.test.ecs;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.scripting.api.*;
import com.terminalvelocitycabbage.engine.scripting.api.registry.*;
import com.terminalvelocitycabbage.engine.scripting.core.CoreLibrary;
import com.terminalvelocitycabbage.engine.scripting.core.CoreTypes;
import com.terminalvelocitycabbage.engine.scripting.parser.*;
import com.terminalvelocitycabbage.engine.scripting.parser.core.EventBlockHeaderParser;
import com.terminalvelocitycabbage.engine.scripting.parser.data.ScriptBlock;
import com.terminalvelocitycabbage.engine.scripting.parser.data.ScriptLine;
import com.terminalvelocitycabbage.engine.scripting.parser.data.SentenceNode;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRAction;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRBlock;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRPrinter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        public static final Identifier EVENT =
                TerminalVelocityEngine.identifierOf("game_started");

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
    void testIRPrintScript() {

        // --- registries ---
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

        // --- types ---
        ScriptType GAME_TYPE =
                scriptTypeRegistry
                        .register(ScriptType.of(CoreLibrary.CORE_NAMESPACE, "game"))
                        .getElement();

        // --- event ---
        ScriptEvent<GameStartEvent> gameStartEvent =
                ScriptEvent.<GameStartEvent>builder()
                        .id(new Identifier("test", "game_start"))
                        .eventClass(GameStartEvent.class)
                        .exposedValue("game", GAME_TYPE, GameStartEvent::getGame)
                        .doc("Fires when the game has fully started.")
                        .build();

        scriptEventRegistry.register(gameStartEvent);

        // --- property ---
        ScriptProperty<Game, String> gameNameProperty =
                new ScriptProperty<>(
                        new Identifier("test", "game.name"),
                        GAME_TYPE,
                        CoreTypes.TEXT,
                        new PropertyAccess.ReadOnly<>(Game::getName),
                        ScriptVisibility.PUBLIC,
                        "The game name."
                );

        scriptPropertyRegistry.register(gameNameProperty);

        // --- actions ---
        ScriptAction printAction =
                scriptActionRegistry.get(
                        new Identifier(CoreLibrary.CORE_NAMESPACE, "print")
                );

        String scriptText = """
                on game_start:
                    print game.name
                """;

        BlockHeaderParserRegistry headerParsers = new BlockHeaderParserRegistry();

        headerParsers.register(new Identifier("test", "event_parser"), new EventBlockHeaderParser());

        List<ScriptBlock> blocks = ScriptBlockParser.parse(scriptText);

        ParsingContext context = new ParsingContext(scriptEventRegistry, scriptTypeRegistry);

        List<IRBlock> irBlocks = new ArrayList<>();

        for (ScriptBlock block : blocks) {

            IRBlock irBlock =
                    headerParsers.parse(block, context);

            for (ScriptLine line : block.body()) {
                SentenceNode sentence =
                        SentenceParser.parse(line);

                IRAction action =
                        SentenceToIRAction.parse(sentence, context);

                irBlock.body().add(action);
            }

            irBlocks.add(irBlock);
        }

        for (IRBlock ir : irBlocks) {
            System.out.println(IRPrinter.print(ir));
        }

    }

    @Test
    void testHelloWorldScript() {

        // --- registries ---
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

        // --- types ---
        ScriptType GAME_TYPE =
                scriptTypeRegistry
                        .register(ScriptType.of(CoreLibrary.CORE_NAMESPACE, "game"))
                        .getElement();

        // --- event ---
        ScriptEvent<GameStartEvent> gameStartEvent =
                ScriptEvent.<GameStartEvent>builder()
                        .id(new Identifier("test", "game_start"))
                        .eventClass(GameStartEvent.class)
                        .exposedValue("game", GAME_TYPE, GameStartEvent::getGame)
                        .doc("Fires when the game has fully started.")
                        .build();

        scriptEventRegistry.register(gameStartEvent);

        // --- property ---
        ScriptProperty<Game, String> gameNameProperty =
                new ScriptProperty<>(
                        new Identifier("test", "game.name"),
                        GAME_TYPE,
                        CoreTypes.TEXT,
                        new PropertyAccess.ReadOnly<>(Game::getName),
                        ScriptVisibility.PUBLIC,
                        "The game name."
                );

        scriptPropertyRegistry.register(gameNameProperty);

        // --- actions ---
        ScriptAction printAction =
                scriptActionRegistry.get(
                        new Identifier(CoreLibrary.CORE_NAMESPACE, "print")
                );

        /*
         * Script:
         *
         * on game_started:
         *     print(game.name)
         *
         * Slot layout:
         *   0 -> game
         *   1 -> game.name
         */

        List<ScriptInstruction> instructions = List.of(

                // load event.game -> slot 0
                new LoadEventValueInstruction(
                        gameStartEvent.exposedValues().get("game"),
                        0
                ),

                // load game.name -> slot 1
                new LoadPropertyInstruction(
                        gameNameProperty,
                        0,
                        1
                ),

                // print(value = slot 1)
                new CallActionInstruction(
                        printAction,
                        new int[]{1},                  // argument slots
                        Map.of("value", 0),             // name -> argument index
                        -1                              // void
                )
        );

        // --- execution ---
        GameStartEvent event =
                new GameStartEvent(new Game("test name"));

        ExecutionContext context =
                new ExecutionContext(event, 2);

        for (ScriptInstruction instruction : instructions) {
            instruction.execute(context);
        }
    }
}

