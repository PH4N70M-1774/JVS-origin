# JVS-origin
This repository contains the source code of the `JVS Programming language`.

## What is JVS?
JVS is a programming language under development. This language is inspired by Java. It provides OOP features in a very simple manner.


## 📘 JVS Language Syntax Specification

### 📦 Package and Import

```jvs
package com.example.subpkg;

import com.example.pkg.*;
import com.example.**; // includes subpackages too
```

---

### 🧱 Class Declaration

```jvs
modifiers jvs ClassName inherits Base1, Base2 implements Interface1, Interface2.
```

- **Multiple inheritance** is allowed using `inherits`.
- `implements` is used for interfaces.
- `modifiers` can include: `public`, `private`, `final`, `static`, `native`.

---

### 🔣 Primitive Types

```jvs
int a = 5;
float b = 3.14;
char c = 'x';
boolean flag = true;
const int MAX = 100;
var x = 42;
```

- `double` is allowed as alias for `float`.
- Use `const` for constant values.
- `var` allows type inference.

---

### 🔐 Modifier Scope Blocks

In **JVS**, you can group members using modifier blocks like `public {}`, `private {}`, etc., to avoid repeating modifiers on each member.

---

#### ✅ Syntax

```jvs
public {
    int id;
    func display(){
        //Implementation
    }
}

public static{
    int users;
}

private {
    int secretCode;
    func encrypt(){
        //Implementation
    }
}

static {
    int counter;
    func increment(){
        //Implementation
    }
}

final {
    const int MAX = 100;
}
```
---
### 🧾 Methods

#### Method with Return Type

```jvs
modifiers func methodName(params): ReturnType {
    // body
}
```

#### Void Method

```jvs
modifiers func methodName(params) {
    // body
}
```

#### Constructor

```jvs
modifiers _init() {
    // body
}
```

---

### 🔁 Control Structures

#### If-Else Ladder

```jvs
if (condition) {
    // block
} elif (condition2) {
    // block
} else {
    // block
}
```

#### Switch Statement

```jvs
switch (expr) {
    case 1 -> {
        // block
    }
    case 2 -> {
        // block
    }
    default -> {
        // block
    }
}
```

#### Loops

```jvs
for (int i = 0; i < n; i++) {
    // block
}

for each (type element : array) {
    // block
}

for ever {
    // infinite loop
}

for times n {
    // repeat block n times
}

while (condition) {
    // block
}

do {
    // block
} while (condition);
```

---

### 🧵 Comments

```jvs
// Single-line comment

/*
   Multi-line
   comment
*/
```

---

### 🔤 String and Print

```jvs
String name="John";
print("Hello, ", name);
```

- Use `,` for concatenation.
- `print` is inline (no newline).

---

### 🧰 Type Casting

```jvs
int a = (int) 5.6;
float b = (float) 10;
```

- Same casting rules as Java.

---

### 🔍 Annotations

```jvs
annotation MyAnnotation {
    // definition
}
```

---

### 🧩 Interfaces and Enums

#### Interface

```jvs
interface MyInterface {
    func method();
}
```

#### Enum

```jvs
enum Color {
    RED, GREEN, BLUE;
}
```

---

### 🧨 Exception Handling

```jvs
try {
    // code
} catch (ExceptionType e) {
    // handle
} finally {
    // cleanup
}
```

---

### 📦 Arrays

```jvs
int[] nums = {1, 2, 3};
char[] chars = {'a', 'b'};
```

---

### 🧬 Generics

```jvs
jvs Box<T> {
    T item;
}
```

- Follows Java-style generics.

---

### 🧮 Operators

| Operator | Meaning       |
|----------|---------------|
| `+`      | Addition       |
| `-`      | Subtraction    |
| `*`      | Multiplication |
| `/`      | Division       |
| `%`      | Modulus        |
| `^`      | Power          |
| `^^`     | Root           |
| `==`     | Equality       |
| `!=`     | Not Equal      |
| `&&`     | AND            |
| `\|\|`     | OR             |
| `!`      | NOT            |
| `>` `<` `>=` `<=` | Comparisons |
| `><`     | Swap values |

---

## 🏷 Reserved Keywords

```
jvs, inherits, implements, package, import,
func, var, const,
final, native, static, public, private,
int, float, char, boolean, void,
if, elif, else, switch, case, default,
for, for each, for ever, for times, while, do,
try, catch, finally, annotation, interface, enum,
true, false, null, self, new, return, break, continue
```

---

## 🧪 Example Program

```jvs
package app.core;

public jvs HelloWorld {

    public{
        _init() {
            print "Creating HelloWorld";
        }

        static func main() {
            String name = "JVS";
            print "Hello, ", name;
        }
    }
}
```

---


# ⚙️ How JVS Works — Compilation and Execution Pipeline

JVS is a high-level programming language that compiles to custom virtual machine instructions. It supports **two compilation backends** targeting two distinct virtual machines:

- `jvsc` → **ArchVM** (text-based, register model)
- `jvsz` → **VeloxVM** (binary, stack-based)

---

## 🧭 Overview

```
JVS Source Code (.jvs)
        │
        ├────────────┬
        │            │
   [jvsc Compiler] [jvsz Compiler]
        │            │
    ArchVM Code     VeloxVM Code
       (.jvse)    (.jvelox)
        │            │
   [ArcVM Runner]  [VeloxVM Runner]
```

---

## 🛠️ Compilation Details

### 🔷 1. `jvsc` — ArchVM Compiler

- Translates `.jvs` files to **ArchVM instruction set**
- Output is `.jvse` (text)
- Target VM: `ArchVM`, which is **register-based**

#### ✅ Features:
- Named registers
- Direct memory access via `VarManager`
- Optimized instruction map: `Map<Integer, Instruction>`
- Example output:
  ```text
  put int, a, 4
  put int, b, 5
  put, int, c
  add a, b, c
  print "Sum = ", c
  ```

---

### 🟦 2. `jvsz` — VeloxVM Compiler

- Translates `.jvs` files to **VeloxVM instruction set**
- Output is a binary `.jvelox` file
- Target VM: `VeloxVM`, which is **stack-based**

#### ✅ Features:
- Stack-oriented instructions
- Operates on values pushed to the stack
- Constant pool for strings and literals
- Example disassembled output (conceptual):
  ```
  ICONST 4
  ICONST 5
  IADD
  PRINTSP 0
  PRINT
  ```

---

## 🧮 Virtual Machines

### 🖥️ ArchVM (Register-based)

- Executes instructions by line number
- Fast lookup via `Map<Integer, Instruction>`
- Stores variables in `VariableList`
- Two-level memory: main and internal (`$temp` vars)
- Optimized for logic-heavy programs

### 🖥️ VeloxVM (Stack-based)

- Stack used for all evaluation
- Simpler instruction decoding
- Separate constant pool for strings
- Efficient for compact bytecode execution
- Supports binary `.jvelox` format

---

## 🔃 Summary

| Aspect           | ArchVM                | VeloxVM             |
|------------------|----------------------|----------------------|
| Compiler         | `jvsc`               | `jvsz`               |
| File Extension   | `.jvse`    | `.jvelox`            |
| Execution Model  | Register-based       | Stack-based          |
| VM Name          | ArchVM                | VeloxVM              |
| Suitable For     | Logic-heavy, readable| Binary, compact code |
| Instruction Form | Text       | Pure Binary          |
| Variable Access  | Named registers      | Stack operations     |
| Advantage  | Easier debugging      | Small and compact files     |

---

## 🚀 Execution

### ✅ ArchVM Execution

```bash
jvsr program.jvsb
```

- Loads all instructions into memory
- Uses direct line mapping
- Executes via optimized loop and jump handling

### ✅ VeloxVM Execution

```bash
jveloxr program.jvelox
```

- Loads bytecode as integer sequence
- Stack-based evaluation
- Handles constants via constant pool index

---

## 📂 Example JVS Code (Input for both compilers)

```jvs
public jvs Main {

    public static func main() {
        var a = 10;
        var b = 20;
        var c = a + b;
        print "Result: ", c;
    }
}
```

- Compiled by `jvsc` → ArchVM text/binary
- The `.jvse` file for this code:
```
put int, a, 10
put int, b, 20
put int, c
add a, b, c
print "Result: ", c
```
- Compiled by `jvsz` → Velox binary
- The disassembled `.jvelox` file for this code:
```
ICONST, 10,
ICONST, 20,
IADD,
PRINTSP, 0,
PRINT,
EXIT
```

---



