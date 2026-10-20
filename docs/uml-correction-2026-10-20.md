# UML documentation correction

Correction date: **20 October 2026**.

Source baseline: **1ea6a1082c81f62642595c172961f7cbde08d6d5**.

The previous diagram showed principal members. This correction expands it to every authored Java class, field, method, and explicitly declared constructor in the same source baseline. It also includes visibility, parameter and return types, declared throws clauses, static/final markers, device multiplicities, and client/test dependencies.

| Declaration | Application | Tests | Total |
| --- | ---: | ---: | ---: |
| Classes | 8 | 1 | 9 |
| Fields | 19 | 2 | 21 |
| Methods | 40 | 26 | 66 |
| Explicit constructors | 1 | 0 | 1 |

The main demonstration creates one facade and six device objects and reuses them for two movie sessions. This is the pattern-object count; it excludes Java library objects. The static demonstration class is not instantiated.

Method parameters appear in UML signatures. All 31 local and lambda parameter declarations are listed separately in Appendix A because local variables are not class attributes. Field initializers, method annotations, source paths, line numbers, and source SHA-256 hashes are recorded in the inventory.

## Updated files

- docs/class-diagram.puml: complete editable UML.
- docs/class-diagram.png: complete diagram including the test class.
- docs/code-inventory.json: declarations extracted using the JDK compiler tree API.
- docs/uml-inventory.md: complete textual declaration and local-variable register.
- reports/home-theater-facade-report.docx: diagram replaced and source appendix added.

The revised report has six pages. The UML page uses A3 landscape for readable text; the remaining pages use A4 portrait. Every rendered page was visually inspected.

The Java implementation and tests are unchanged. The earlier report and diagram remain recoverable from commit 1ea6a10. The documentation correction uses its actual date and does not alter earlier Git history.
