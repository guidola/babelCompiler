package edu.salleurl.g6.ase;

import taulasimbols.*;

public class Ase {
    private TaulaSimbols ts;

    public Ase() {
        this.ts = new TaulaSimbols();
    }

    public void addNewBlock(){
        ts.inserirBloc(new Bloc());
        ts.setBlocActual(ts.getNumeroBlocs()-1);
    }
    public void addNewConstant(Constant var) {
        if(ts.obtenirBloc(ts.getBlocActual()).obtenirConstant(var.getNom())==null)
            ts.obtenirBloc(ts.getBlocActual()).inserirConstant(var);
        else
            System.err.println("Existent constant! Value: "+var.getValor()+" Name: "+var.getNom());
        //System.out.println("Added new const");
    }
    public void addNewVar(Variable var) {
        if(ts.obtenirBloc(ts.getBlocActual()).obtenirConstant(var.getNom())==null)
            ts.obtenirBloc(ts.getBlocActual()).inserirVariable(var);
        else
            System.err.println("Existent variable! Type: "+var.getTipus().getNom()+" Name: "+var.getNom());

        //System.out.println("Added new var");
    }

    public void addNewFuncio(Funcio var){
        System.out.println("FUNCIO: "+ var.getNom());
        if(ts.obtenirBloc(ts.getBlocActual()).obtenirProcediment(var.getNom())==null)
            ts.obtenirBloc(ts.getBlocActual()).inserirProcediment(var);
        else
            System.err.println("Existent function! Type: "+var.getTipus().getNom()+" Name: "+var.getNom());

    }
    public void deleteActualBlock(){
        ts.esborrarBloc(ts.getBlocActual());
        System.out.println("Deleted block: "+ts.getBlocActual());
        ts.setBlocActual(ts.getBlocActual()-1);
        System.out.println("Blocks availble: "+ts.getBlocActual());
    }

}
