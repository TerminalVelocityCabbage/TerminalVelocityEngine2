**Functional/Structural Features:**
*   **Margins**: similar to padding, but applied to the outside of an element's bounding box.

**Advanced Visuals (NanoVG Strengths):**
*   **Images & Vector Graphics**: (Pending) Data structures exist, but the NanoVG `nvgImagePattern` and path rendering logic are not yet implemented in `renderElement`.
*   **Gradients**: NanoVG supports excellent linear and radial gradients which can be added to `ElementDeclaration`.
*   **Drop/Box Shadows**: Using box gradients, we can implement soft shadows or "glow" effects on containers.

**Future Plans:**
*   **Tailwind-style API**: (Pending) String-based shorthand (e.g., `container("bg-red-500 p-4")`) to replace the current builders.
*   **Animations**: Implementing basic animation primitives like transitions to enhance user interface interactivity.
*   **Graphs**: (Planned) Will require a specialized `UIRenderNode` function that translates data sets into NanoVG paths (lines, area fills).
*   **Custom Render Callbacks**: Support for `CustomElementConfig`, allowing users to provide a lambda or RenderNode for raw NanoVG drawing inside a layout element's bounding box.
