# TVE UI System

The TVE UI System leverages many concepts from immediate mode UI libraries you're used to as well as some concepts from web development. The layout system is heavily based around [Clay](https://github.com/nicbarker/clay) which is heavily based around flexbox, so if you know either of those you will feel right at home.

The UI system is tightly integrated into the other core concepts of this engine, but if you know how this engine works - creating UIs will feel effortless compared to manually generating them with Quads.

## Getting Started
The first thing a user need to do to create a UI is to have a render graph setup (we assume you've already done this). Then all you need to do is add a new render node that extends ``UIRenderNode`` and Override the ``declareUI`` method (required). For example:

```java
public class DrawUIRenderNode extends UIRenderNode {

    public DrawTestUIRenderNode(ShaderProgramConfig shaderProgramConfig) {
        super(shaderProgramConfig);
    }

    @Override
    protected void declareUI() {
        //TODO define UI layout here
    }
}
```

A `UIRenderNode` is effectivley the same thing as a regular RenderNode that you would extend to render your scenes with, but it provides a few key utilities for declaring UI. Since this is immediate mode UI your interface layout is regenerated every frame (best case for games most of the time), so all you need to do is define the interface with strings of methods like this:

```java
@Override
protected void declareUI() {
    text("Hello World!", "");
}
```
This will render some text to the screen. This isn't the only type of element you can create though, you can also create containers:
```java
@Override
protected void declareUI() {
    container("", () -> {});
}
```
You'll notice that we have an empty lambda expression as the second parameter here. This is how you pass children to your container:
```java
@Override
protected void declareUI() {
    container("", () -> {
        text("Hello World!", "");
    });
}
```
Now "Hello World!" will be rendered as a child of container.

## Componentizing UIs
Since this is a immediate mode UI you can easily define new components on top of the provided `container` and `text` etc functions like so:
```java
private UIElement label(String labelText) {
    return container("", () -> {
        text(labelText);
    });
}
```

## Styling and Configuring the Layout

You'll notice in the previous examples that we've passed in an empty string to the container and the text elements. These are for configuring the elements appearance and layout.

We've borrowed a lot of syntax from [TailwindCSS](https://tailwindcss.com/) for our UI system, so if you've used that this will feel familiar, however we've had to make some changes to make this more appropriate to game development. Let's look at our previous example:
```java
@Override
protected void declareUI() {
    container("p-[5px] bg-[1,0,0] grow", () -> {
        text("Hello World!", "color-[1,1,1]");
    });
}
```
Now our container will be fullscreen and red with a padding of 5px all the way around so our now white text will render 5px from the container's border

## Style/Layout Syntax
For this guide anything in parentheses () is a user defined value:

### Types:
These are the variables that can be passed to configure an element used in future sections of this guide
```terminaloutput
(dim)       a dimension and unit ex: 5px (5 pixels) or 5% (5 percent of parent dimension)
(col)       a color defined by 3 or 4 components in RGB ex: [1,1,1] (white), or [1,1,1,0.5] (white at 50% opacity)
(rat)       a ratio ex: 1/2 or 4/3
(axis)      an axis: x, y, vertical, horizontal, vert, horiz, both
(xdir)      a direction: left right center
(ydir)      a direction: top bottom center
(xlay)      a layout direction: top-to-bottom, ttb, bottom-to-top, btt
(ylay)      a layout direction: left-to-right, ltr, right-to-left, rtl
(id)        a hashable element id (will be converted to an int later)
(apt)       an attachment point: top, bottom, left, right, center, top-left, top-right, bottom-left, bottom-right
```

### Padding:
Defines spacing *inside* the current element
```terminaloutput
p-[(dim)]   padding applied to all sides
pt-[(dim)]  padding applied to the top
pb-[(dim)]  padding applied to the botthom
pl-[(dim)]  padding applied to the left
pr-[(dim)]  padding applied to the right
px-[(dim)]  padding applied on the x axis (left and right)
py-[(dim)]  padding applied on the y axis (top and bottom)
```

### Margins:
Defines spacing *outside* the current element
```terminaloutput
m-[(dim)]   margin applied to all sides
mt-[(dim)]  margin applied to the top
mb-[(dim)]  margin applied to the botthom
ml-[(dim)]  margin applied to the left
mr-[(dim)]  margin applied to the right
mx-[(dim)]  margin applied on the x axis (left and right)
my-[(dim)]  margin applied on the y axis (top and bottom)
```

### Background color:
Colors the background of this element with a solid color
```terminaloutput
bg-[(col)]  solid background color on container element
```

### Corner Rounding:
Rounds the corners of the current element
```terminaloutput
rounded-[(dim)]     corner radius applied to all corners
roundedt-[(dim)]    corner radius applied to the top two corners
roundedb-[(dim)]    corner radius applied to the botthom two corners
roundedl-[(dim)]    corner radius applied to the left two corners
roundedr-[(dim)]    corner radius applied to the right two corners
roundedtr-[(dim)]   corner radius applied to the top right corner
roundedtl-[(dim)]   corner radius applied on the top left corner
roundedbr-[(dim)]   corner radius applied on the bottom right corner
roundedbl-[(dim)]   corner radius applied on the bottom left corner
```

### Borders:
Configures the border of a container
```terminaloutput
border-width-[(dim)]   border thickness applied to all sides
bordert-width-[(dim)]  border thickness applied to the top
borderb-width-[(dim)]  border thickness applied to the botthom
borderl-width-[(dim)]  border thickness applied to the left
borderr-width-[(dim)]  border thickness applied to the right
borderx-width-[(dim)]  border thickness applied on the x axis (left and right)
bordery-width-[(dim)]  border thickness applied on the y axis (top and bottom)

border-color-[(col)]   border color applied to all sides
bordert-color-[(col)]  border color applied to the top
borderb-color-[(col)]  border color applied to the botthom
borderl-color-[(col)]  border color applied to the left
borderr-color-[(col)]  border color applied to the right
borderx-color-[(col)]  border color applied on the x axis (left and right)
bordery-color-[(col)]  border color applied on the y axis (top and bottom)
```

### Aspect Ratio:
Allows the user to control the size of an element in a responsive manner with only one dimension supplied to maintain an aspect ratio
```terminaloutput
aspect-[(rat)]
```

### Clip:
Allows the element to clip overflow elements (scrolling containers do this behind the scenes, but also add a scrollbar to manage it)
```terminaloutput
clip-[(axis)]           clips the specified axis
clip-offset-x[(dim)]    offsets the elements inside the clip bounds by the offset in the x direction
clip-offset-y[(dim)]    offsets the elements inside the clip bounds by the offset in the y direction
```

### Sizing:
Manages the size of an element in a fixed or responsive way
```terminaloutput
w-[(dim)]           sets the width of this element to a fixed dimension
h-[(dim)]           sets the height of this element to a fixed dimension
min-w-[(dim)]       sets the min width of this element to a fixed dimension (fit with min no max)
min-h-[(dim)]       sets the min height of this element to a fixed dimension (fit with min no max)
max-w-[(dim)]       sets the max width of this element to a fixed dimension (grow with max)
max-h-[(dim)]       sets the max height of this element to a fixed dimension (grow with max)
grow                allows this element to grow to the max size it's children and parent will allow
grow-x              allows the width of this element to grow to the max size it's children and parent will allow
grow-y              allows the height of this element to grow to the max size it's children and parent will allow
min-grow-x-[(dim)]  allows the width of this element to grow to the max size it's children and parent will allow and will not be compressed below the specified dim
max-grow-x-[(dim)]  allows the width of this element to grow to the max size it's children and parent will allow up to the specified dim
min-grow-y-[(dim)]  allows the height of this element to grow to the max size it's children and parent will allow and will not be compressed below the specified dim
max-grow-y-[(dim)]  allows the height of this element to grow to the max size it's children and parent will allow up to the specified dim
fit                 sets the width and height of this element to the min dimension possible to fit it's children
fit-x               sets the width of this element to the min dimension possible to fit it's children
fit-y               sets the height of this element to the min dimension possible to fit it's children
min-fit-x-[(dim)]   sets the width of this element to the min dimension possible to fit it's children with a minimum dimension of what is specified
max-fit-x-[(dim)]   sets the width of this element to the min dimension possible to fit it's children with a maximum dimension of what is specified
min-fit-y-[(dim)]   sets the height of this element to the min dimension possible to fit it's children with a minimum dimension of what is specified
max-fit-y-[(dim)]   sets the height of this element to the min dimension possible to fit it's children with a maximum dimension of what is specified
```

### Spacing Between Children
Manages the gap between siblings
```terminaloutput
gap-[(dim)]
```

### Alignment
Manages alignment of children
```terminaloutput
align-x-[(xdir)]     aligns the children of this element to the side of this element specified horizontally
align-y-[(ydir)]     aligns the children of this element to the side of this element specified vertically
```

### Layout Direction
Manages the position of siblings relative to other siblings
```terminaloutput
layout-x-[(xlay)]
layout-y-[(ylay)]
```

## Floating Elements
//TODO there is more that needs to go here
Floating elements are elements not positioned by the layout engine, but rather specifically told how to layout by the user
```terminaloutput
float-parent            tells the layout engine that this element is floating it's position is relative to the parent
float-root              tells the layout engine that this element is floating relative to the root element
float-element-[(id)]    tells the layout engine that this element is floating relative to the specified element id

attach-[(apt)]          attaches this element based on the point specified
to-[(apt)]              attaches the attachement point to the parent's attachment point specified (parent refers to root/parent/element whatever float type is specified)

float-offset-[(dim)]    offsets the element by the dim relative to the attachment point
```

## Wrapping
Enables element wrapping
```terminaloutput
wrap        wraps child elements of this container based on layout direction
```