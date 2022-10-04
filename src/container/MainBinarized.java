package container;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import utils.ContainerInfo;

public class MainBinarized {

	public static void main(String[] args) {

		try {

			IloCplex modelo = new IloCplex();

			IloNumVar x[][][] = new IloNumVar[ContainerInfo.n_intens][ContainerInfo.k_containers][ContainerInfo.limite_unidades
					+ 1];

			for (int i = 0; i < ContainerInfo.n_intens; i++) {

				for (int j = 0; j < ContainerInfo.k_containers; j++) {

					for (int m = 0; m <= ContainerInfo.limite_unidades; m++) {

						x[i][j][m] = modelo.boolVar();
					}
				}
			}

			IloLinearNumExpr funcaoObjetivo = modelo.linearNumExpr();
			for (int i = 0; i < ContainerInfo.n_intens; i++) {
				for (int j = 0; j < ContainerInfo.k_containers; j++) {

					for (int m = 0; m <= ContainerInfo.limite_unidades; m++) {

						funcaoObjetivo.addTerm(ContainerInfo.lucros[i] * m, x[i][j][m]);

					}
				}
			}

			// Função Objetivo
			modelo.addMaximize(funcaoObjetivo);

//			Restrição (6)

			for (int i = 0; i < ContainerInfo.n_intens; i++) {

				for (int j = 0; j < ContainerInfo.k_containers; j++) {

					IloLinearNumExpr restricaoDeQuantidade = modelo.linearNumExpr();

					for (int m = 0; m <= ContainerInfo.limite_unidades; m++) {

						restricaoDeQuantidade.addTerm(1, x[i][j][m]);

					}

					modelo.addLe(restricaoDeQuantidade, 1);

				}

			}

			for (int j = 0; j < ContainerInfo.k_containers; j++) {

				IloLinearNumExpr restricaoDePeso = modelo.linearNumExpr();

				for (int i = 0; i < ContainerInfo.n_intens; i++) {

					for (int m = 0; m <= ContainerInfo.limite_unidades; m++) {

						restricaoDePeso.addTerm(ContainerInfo.pesos[i] * m, x[i][j][m]);
					}
				}
				modelo.addLe(restricaoDePeso, ContainerInfo.cap_carga);

			}

			for (int j = 0; j < ContainerInfo.k_containers; j++) {

				IloLinearNumExpr restricaoDeVolume = modelo.linearNumExpr();

				for (int i = 0; i < ContainerInfo.n_intens; i++) {

					for (int m = 0; m <= ContainerInfo.limite_unidades; m++) {

						restricaoDeVolume.addTerm(ContainerInfo.volumes[i] * m, x[i][j][m]);
					}
				}
				modelo.addLe(restricaoDeVolume, ContainerInfo.cap_volumetrica);
			}

			// resolver...
			if (modelo.solve()) {
				System.out.println("Status:\t" + modelo.getStatus());

				System.out.println("Lucro:\t" + modelo.getObjValue());

				System.out.println();

			}

		} catch (

		IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
