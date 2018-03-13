# Gramática babel 2018

1. P -> *Declaracio* **inici** *llista\_inst* **fi**
* Declaracio -> *llista\_dec\_var* *llista\_dec\_func*
* llista\_dec\_var -> *dec\_var* *llista\_dec\_var*
* llista\_dec\_var -> **∑**
* llista\_dec\_func -> *dec\_func* *llista\_dec\_func*
* llista\_dec\_func -> **∑**
* dec\_var -> **const id =** *exp* **;**
* dec\_var -> *Tipus* **id ;**
* dec\_func -> **funcio id (** *llista\_param* **) :** *tipus\_simple* **{** *llista\_dec\_var *llista\_inst* **} ;**
* llista\_param -> **∑**
* llista\_param -> *Tipus* *is\_ref* **id** *llista\_param\_aux*
* is\_ref -> **∑**
* is\_ref -> **&**
* llista\_param\_aux -> **∑**
* llista\_param\_aux -> **,** *llista\_param*
* tipus -> **tipus\_simple**
* tipus -> **vector [ cte\_entera ] de tipus\_simple**
* exp -> *exp\_simple* *llista\_exp\_simple*
* llista\_exp\_simple -> **∑**
* llista\_exp\_simple -> **oper\_rel** *exp\_simple*
* exp\_simple -> *opu* *terme* *llista\_termes*
* opu -> **sao**
* opu -> **not**
* opu -> **∑**
* llista\_termes -> *ops* *terme* *llista_termes*
* ops -> **sao** 
* ops -> **or**
* llista\_termes -> **∑**
* terme -> *factor* *factor_aux*
* factor\_aux -> **∑**
* factor\_aux -> *opb* *terme*
* opb -> **cao**
* opb -> **and**
* factor -> **cte_entera**
* factor -> **cte_logica**
* factor -> **cte_cadena**
* factor -> **id** *factor\_id\_sufix*
* factor -> **(** *exp* **)**
* factor\_id\_sufix -> **(** *llista_exp* **)**
* factor\_id\_sufix -> *is_vector*
* is\_vector -> **∑**
* is\_vector -> **[** *exp* **]**
* llista\_exp -> **∑**
* llista\_exp -> *llista\_exp\_non\_empty*
* llista\_exp\_non\_empty -> *exp* *llista\_exp\_aux*
* llista\_exp\_aux -> **∑**
* llista\_exp\_aux -> **,** *llista\_exp\_non\_empty*
* variable -> **id** *is_vector* 
* llista\_inst -> *inst* ; *llista\_inst\_aux*
* llista\_inst\_aux -> **∑**
* llista\_inst\_aux -> *llista\_inst*
* inst -> *variable* **=** *exp*
* inst -> **escriure (** *llista\_exp\_non\_empty* **)**
* inst -> **llegir (** *llista_variable* **)**
* llista\_variable -> *variable* *llista\_var\_aux*
* llista\_var\_aux -> **∑**
* llista\_var\_aux -> **,** *llista_variable*
* inst -> **repetir** *llista_inst* **fins** *exp*
* inst -> **mentre** *exp* **fer** *llista_inst* **fimentre**
* inst -> **si** *exp* **llavors** *llista_inst* *has_sino* **fisi**
* has\_sino -> **∑**
* has\_sino -> **sino** *llista_inst*
* inst -> **retornar** *exp*
