package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.ActionContext;
import com.terminalvelocitycabbage.engine.scripting.api.ScriptAction;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRValue;

import java.util.HashMap;
import java.util.Map;

public final class CallActionInstruction implements ScriptInstruction {

    private final ScriptAction action;
    private final Map<String, IRValue> arguments;

    public CallActionInstruction(
            ScriptAction action,
            Map<String, IRValue> arguments
    ) {
        this.action = action;
        this.arguments = arguments;
    }

    @Override
    public void execute(ExecutionContext context) {

        Map<String, Object> evaluatedArgs = new HashMap<>();

        for (Map.Entry<String, IRValue> entry : arguments.entrySet()) {
            Object value =
                    IRValueEvaluator.evaluate(entry.getValue(), context);
            evaluatedArgs.put(entry.getKey(), value);
        }

        ActionContext actionContext =
                new ActionContext(evaluatedArgs, context);

        action.executor().execute(actionContext);
    }
}


