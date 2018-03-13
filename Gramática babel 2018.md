# Gramática babel 2018

1. P -> *Declaracio* **inici** *llista\_inst* **fi**
2. Declaracio -> *llista\_dec\_var* *llista\_dec\_func*
3. llista\_dec\_var -> *dec\_var* *llista\_dec\_var*
4. llista\_dec\_var -> **∑**
5. llista\_dec\_func -> *dec\_func* *llista\_dec\_func*
6. llista\_dec\_func -> **∑**
7. dec\_var -> **const id =** *exp* **;**
8. dec\_var -> *Tipus* **id ;**
9. dec\_func -> **funcio id (** *llista\_param* **) :** **tipus\_simple** **{** *llista\_dec\_var* *llista\_inst* **} ;**
10. llista\_param -> **∑**
11. llista\_param -> *Tipus* *is\_ref* **id** *llista\_param\_aux*
12. is\_ref -> **∑**
13. is\_ref -> **amps**
14. llista\_param\_aux -> **∑**
15. llista\_param\_aux -> **,** *llista\_param*
16. tipus -> **tipus\_simple**
17. tipus -> **vector [ cte\_entera ] de tipus\_simple**
18. exp -> *exp\_simple* *llista\_exp\_simple*
19. llista\_exp\_simple -> **∑**
20. llista\_exp\_simple -> **oper\_rel** *exp\_simple*
21. exp\_simple -> *opu* *terme* *llista\_termes*
22. opu -> **sao**
23. opu -> **not**
24. opu -> **∑**
25. llista\_termes -> *ops* *terme* *llista_termes*
26. ops -> **sao** 
27. ops -> **or**
28. llista\_termes -> **∑**
29. terme -> *factor* *factor_aux*
30. factor\_aux -> **∑**
31. factor\_aux -> *opb* *terme*
32. opb -> **cao**
33. opb -> **and**
34. factor -> **cte_entera**
35. factor -> **cte_logica**
36. factor -> **cte_cadena**
37. factor -> **id** *factor\_id\_sufix*
38. factor -> **(** *exp* **)**
39. factor\_id\_sufix -> **(** *llista_exp* **)**
40. factor\_id\_sufix -> *is_vector*
41. is\_vector -> **∑**
42. is\_vector -> **[** *exp* **]**
43. llista\_exp -> **∑**
44. llista\_exp -> *llista\_exp\_non\_empty*
45. llista\_exp\_non\_empty -> *exp* *llista\_exp\_aux*
46. llista\_exp\_aux -> **∑**
47. llista\_exp\_aux -> **,** *llista\_exp\_non\_empty*
48. variable -> **id** *is_vector* 
49. llista\_inst -> *inst* ; *llista\_inst\_aux*
50. llista\_inst\_aux -> **∑**
51. llista\_inst\_aux -> *llista\_inst*
52. inst -> *variable* **op_rel** *exp*
53. inst -> **escriure (** *llista\_exp\_non\_empty* **)**
54. inst -> **llegir (** *llista_variable* **)**
55. llista\_variable -> *variable* *llista\_var\_aux*
56. llista\_var\_aux -> **∑**
57. llista\_var\_aux -> **,** *llista_variable*
58. inst -> **repetir** *llista_inst* **fins** *exp*
59. inst -> **mentre** *exp* **fer** *llista_inst* **fimentre**
60. inst -> **si** *exp* **llavors** *llista_inst* *has_sino* **fisi**
61. has\_sino -> **∑**
62. has\_sino -> **sino** *llista_inst*
63. inst -> **retornar** *exp*
