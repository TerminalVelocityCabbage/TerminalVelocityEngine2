package com.terminalvelocitycabbage.engine.util;
import static java.lang.Math.*;
import static org.joml.Math.sin;

public class Easing {

    private static final float PI = 3.1415f;
    private static final float C1 = 1.70158f;
    private static final float C2 = C1 * 1.525f;
    private static final float C3 = C1 + 1;
    private static final float C4 = (2 * PI) / 3f;
    private static final float C5 = (2 * PI) / 4.5f;
    private static final float N1 = 7.5625f;
    private static final float D1 = 2.75f;

    public enum Direction {

        IN("in"),
        OUT("out"),
        IN_OUT("in_out");

        private final String name;

        Direction(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Function {

        LINEAR("linear"),
        STEP("step"),
        SIN("sin"),
        QUADRATIC("quadratic"),
        CUBIC("cubic"),
        QUARTIC("quartic"),
        QUINTIC("quintic"),
        EXPONENTIAL("exponential"),
        CIRCULAR("circular"),
        BACK("back"),
        ELASTIC("elsatic"),
        BOUNCE("bounce");

        private final String name;

        Function(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static float lerp(float start, float end, float progress, Direction direction, Function function) {
        return (start + (end - start) * ease(direction, function, progress));
    }

    public static float ease(Direction direction, Function function, float progress) {
        return switch (direction) {
            case IN -> easeIn(function, progress);
            case OUT -> easeOut(function, progress);
            case IN_OUT -> easeInOut(function, progress);
        };
    }

    public static float easeIn(Function function, float progress) {
        return switch (function) {
            case LINEAR -> easeInLinear(progress);
            case STEP -> easeInStep(progress);
            case SIN -> easeInSin(progress);
            case QUADRATIC -> easeInQuad(progress);
            case CUBIC -> easeInCubic(progress);
            case QUARTIC -> easeInQuart(progress);
            case QUINTIC -> easeInQuint(progress);
            case EXPONENTIAL -> easeInExpo(progress);
            case CIRCULAR -> easeInCirc(progress);
            case BACK -> easeInBack(progress);
            case ELASTIC -> easeInElastic(progress);
            case BOUNCE -> easeInBounce(progress);
        };
    }

    public static float easeOut(Function function, float progress) {
        return switch (function) {
            case LINEAR -> easeOutLinear(progress);
            case STEP -> easeOutStep(progress);
            case SIN -> easeOutSin(progress);
            case QUADRATIC -> easeOutQuad(progress);
            case CUBIC -> easeOutCubic(progress);
            case QUARTIC -> easeOutQuart(progress);
            case QUINTIC -> easeOutQuint(progress);
            case EXPONENTIAL -> easeOutExpo(progress);
            case CIRCULAR -> easeOutCirc(progress);
            case BACK -> easeOutBack(progress);
            case ELASTIC -> easeOutElastic(progress);
            case BOUNCE -> easeOutBounce(progress);
        };
    }

    public static float easeInOut(Function function, float progress) {
        return switch (function) {
            case LINEAR -> easeInOutLinear(progress);
            case STEP -> easeInOutStep(progress);
            case SIN -> easeInOutSin(progress);
            case QUADRATIC -> easeInOutQuad(progress);
            case CUBIC -> easeInOutCubic(progress);
            case QUARTIC -> easeInOutQuart(progress);
            case QUINTIC -> easeInOutQuint(progress);
            case EXPONENTIAL -> easeInOutExpo(progress);
            case CIRCULAR -> easeInOutCirc(progress);
            case BACK -> easeInOutBack(progress);
            case ELASTIC -> easeInOutElastic(progress);
            case BOUNCE -> easeInOutBounce(progress);
        };
    }

    public static float easeInLinear(float progress) {
        return progress;
    }

    public static float easeOutLinear(float progress) {
        return progress;
    }

    public static float easeInOutLinear(float progress) {
        return progress;
    }

    public static float easeInStep(float progress) {
        return progress > 0 ? 1f : 0f;
    }

    public static float easeOutStep(float progress) {
        return progress > 0 ? 0f : 1f;
    }

    public static float easeInOutStep(float progress) {
        return progress > 0.5 ? 1f : 0f;
    }

    public static float easeInSin(float progress) {
        return (float) (1 - cos((progress * PI) / 2f));
    }

    public static float easeOutSin(float progress) {
        return sin((progress * PI) / 2f);
    }

    public static float easeInOutSin(float progress) {
        return (float) ((-cos(PI * progress) - 1) / 2f);
    }

    public static float easeInQuad(float progress) {
        return progress * progress;
    }

    public static float easeOutQuad(float progress) {
        return 1 - ((1 - progress) * (1 - progress));
    }

    public static float easeInOutQuad(float progress) {
        return progress < 0.5 ? 2 * progress * progress : 1 - (float)pow((-2 * progress) + 2, 2) / 2f;
    }

    public static float easeInCubic(float progress) {
        return progress * progress * progress;
    }

    public static float easeOutCubic(float progress) {
        return 1 - ((1 - progress) * (1 - progress) * (1 - progress));
    }

    public static float easeInOutCubic(float progress) {
        return progress < 0.5 ? 4 * progress * progress * progress : 1 - (float)pow((-2 * progress) + 2, 3) / 2f;
    }

    public static float easeInQuart(float progress) {
        return progress * progress * progress * progress;
    }

    public static float easeOutQuart(float progress) {
        return 1 - (float)pow(1 - progress, 4);
    }

    public static float easeInOutQuart(float progress) {
        return progress < 0.5 ? 8 * progress * progress * progress * progress : 1 - (float)pow(-2 * progress + 2, 4) / 2f;
    }

    public static float easeInQuint(float progress) {
        return progress * progress * progress * progress * progress;
    }

    public static float easeOutQuint(float progress) {
        return 1 - (float)pow(1 - progress, 5);
    }

    public static float easeInOutQuint(float progress) {
        return progress < 0.5 ? 16 * progress * progress * progress * progress * progress : 1 - (float)pow(-2 * progress + 2, 5) / 2f;
    }

    public static float easeInExpo(float progress) {
        return progress == 0 ? 0 : (float)pow(2, 10 * progress - 10);
    }

    public static float easeOutExpo(float progress) {
        return progress == 1 ? 1 : 1 - (float)pow(2, -10 * progress);
    }

    public static float easeInOutExpo(float progress) {
        return progress == 0 ? 0 : progress == 1 ? 1 : progress < 0.5 ? (float)pow(2, 20 * progress - 10) / 2 : (2 - (float)pow(2, -20 * progress + 10)) / 2;
    }

    public static float easeInCirc(float progress) {
        return 1 - (float)sqrt(1 - pow(progress, 2));
    }

    public static float easeOutCirc(float progress) {
        return (float)sqrt(1 - pow(progress - 1, 2));
    }

    public static float easeInOutCirc(float progress) {
        return progress < 0.5 ? (float)(1 - sqrt(1 - pow(2 * progress, 2))) / 2 : (float)(sqrt(1 - pow(-2 * progress + 2, 2)) + 1) / 2;
    }

    public static float easeInBack(float progress) {
        return C3 * progress * progress * progress - C1 * progress * progress;
    }

    public static float easeOutBack(float progress) {
        return (float)(1 + C3 * pow(progress - 1, 3) + C1 * pow(progress - 1, 2));
    }

    public static float easeInOutBack(float progress) {
        return progress < 0.5 ? (float)(pow(2 * progress, 2) * ((C2 + 1) * 2 * progress - C2)) / 2 : (float)(pow(2 * progress - 2, 2) * ((C2 + 1) * (progress * 2 - 2) + C2) + 2) / 2;
    }

    public static float easeInElastic(float progress) {
        return progress == 0 ? 0 : progress == 1 ? 1 : (float)-pow(2, 10 * progress - 10) * (float)sin((progress * 10 - 10.75) * C4);
    }

    public static float easeOutElastic(float progress) {
        return progress == 0 ? 0 : progress == 1 ? 1 : (float)pow(2, -10 * progress) * (float)sin((progress * 10 - 0.75) * C4) + 1;
    }

    public static float easeInOutElastic(float progress) {
        return progress == 0 ? 0 : progress == 1 ? 1 : progress < 0.5
                ? (float) -(pow(2, 20 * progress - 10) * sin((20 * progress - 11.125) * C5)) / 2
                : (float) (pow(2, -20 * progress + 10) * sin((20 * progress - 11.125) * C5)) / 2 + 1;
    }

    public static float easeInBounce(float progress) {
        return 1 - easeOutBounce(1 - progress);
    }

    public static float easeOutBounce(float progress) {
        if (progress < 1 / D1) {
            return N1 * progress * progress;
        } else if (progress < 2 / D1) {
            return N1 * (progress -= 1.5 / D1) * progress + 0.75f;
        } else if (progress < 2.5 / D1) {
            return N1 * (progress -= 2.25 / D1) * progress + 0.9375f;
        } else {
            return N1 * (progress -= 2.625 / D1) * progress + 0.984375f;
        }
    }

    public static float easeInOutBounce(float progress) {
        return progress < 0.5 ? (1 - easeOutBounce(1 - 2 * progress)) / 2 : (1 + easeOutBounce(2 * progress - 1)) / 2;
    }

}
