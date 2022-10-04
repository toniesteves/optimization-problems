package container;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import utils.ContainerInfo;

public class MainNumerical {

	public static void main(String[] args) {

		try {

			// Criando o modelo
			IloCplex modelo = new IloCplex();

			// Variaveis de decisão
			IloNumVar containers[][] = new IloNumVar[ContainerInfo.n_intens][ContainerInfo.k_containers];
			int lower_bound = 0;
			int upper_bound = ContainerInfo.limite_unidades;

			for (int item = 0; item < ContainerInfo.n_intens; item++) {
				for (int container = 0; container < ContainerInfo.k_containers; container++) {
					containers[item][container] = modelo.numVar(lower_bound, upper_bound);
				}
			}

			
			// Restrições
			// Restrição 1: Cada conteiner k possui a mesma capacidade de volume cv.
			for (int container = 0; container < ContainerInfo.k_containers; container++) {

				IloLinearNumExpr restricaoDeVolume = modelo.linearNumExpr();

				for (int item = 0; item < ContainerInfo.n_intens; item++) {

					restricaoDeVolume.addTerm(ContainerInfo.volumes[item], containers[item][container]);
				}

				modelo.addLe(restricaoDeVolume, ContainerInfo.cap_volumetrica);
			}

			
			
			// Restrição 2: Cada conteiner k possui a mesma capacidade de carga cc.

			for (int container = 0; container < ContainerInfo.k_containers; container++) {

				IloLinearNumExpr restricaoDeCarga = modelo.linearNumExpr();

				for (int item = 0; item < ContainerInfo.n_intens; item++) {

					restricaoDeCarga.addTerm(ContainerInfo.pesos[item], containers[item][container]);
				}

				modelo.addLe(restricaoDeCarga, ContainerInfo.cap_carga);
			}
			
			
			// Adicionando Funçao Objetivo

			IloLinearNumExpr funcaoObjetivo = modelo.linearNumExpr();
			for (int item = 0; item < ContainerInfo.n_intens; item++) {

				for (int container = 0; container < ContainerInfo.k_containers; container++) {
					
//					for (int qtd =0; qtd < ContainerInfo.m; qtd++) {
						
						funcaoObjetivo.addTerm(ContainerInfo.lucros[item], containers[item][container]);
//					}

				}
			}

			modelo.addMaximize(funcaoObjetivo);

			// Rodando o solver

			if (modelo.solve()) {
				System.out.println("Model Solved!");
				System.out.println(modelo.getStatus());

				System.out.println("Lucro:\t" + modelo.getObjValue());

				System.out.println();

				for (int item = 0; item < ContainerInfo.n_intens; item++) {
					for (int container = 0; container < ContainerInfo.k_containers; container++) {
						System.out.print(modelo.getValue(containers[item][container]) + "\t");
					}
					System.out.println();
				}

			} else {

				System.out.println("Can't solve the model!");
			}

		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
