package controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JOptionPane;

import model.Boleto;

public class Controller {
	private List<Boleto> listaBoletos = new ArrayList<Boleto>();
	private boolean reset = true;
	private boolean processado=false;
	private int maiorAtraso=0;
	private int menorAtraso=10000;
	private int mediaAtraso=0;
	private double vlrTotalBoletos=0;
	private double vlrTotalMultas=0;
	private double vlrTotalJuros=0;
	
	private int somaDias = 0;

	
	private final double txMulta= 0.02; //2 %
	private final double txJurosMes= 0.012; //1,2%
	private double txJurosDia = txJurosMes / 30;

	
	//getters da classe
	public Boleto[] getListaBoletos() {
		return (Boleto[]) listaBoletos.toArray();
	}

	public boolean isProcessado() {
		return processado;
	}
	public void setProcessado(boolean processado) {
		this.processado = processado;
	}

	public int getMaiorAtraso() {
		return maiorAtraso;
	}

	public int getMenorAtraso() {
		return menorAtraso;
	}


	public int getMediaAtraso() {
		return mediaAtraso;
	}

	public double getvlrTotalBoletos() {
		return vlrTotalBoletos;
	}

	public double getvlrTotalMultas() {
		return vlrTotalMultas;
	}
	
	public double getvlrTotalJuros() {
		return vlrTotalJuros;
	}
	
	public int getQtdeBoletos() {
		return this.listaBoletos.size();
	}
	
	//Metodos publicos da classe
	public String getSummary() {
		DecimalFormat df= new DecimalFormat();
		df.applyPattern("R$ #,##0.00");
		String mensagem="Foram lidos "+this.getQtdeBoletos()+" boletos \n";
		mensagem+="O valor total dos boletos é "+df.format(vlrTotalBoletos)+"\n";
		
		if(this.isProcessado()) {
			mensagem+="O maior atraso em dias é de "+ maiorAtraso + " dias\n"; 
			mensagem+="O menor atraso em dias é de "+ menorAtraso+ " dias\n";
			mensagem+="A media de dias atrasados é de "+ mediaAtraso+ " dias\n";
			mensagem+="O valor total de multas é "+df.format(vlrTotalMultas)+"\n";
			mensagem+="O valor total de juros é "+df.format(vlrTotalJuros);
		}
		return mensagem;
	}
	
	
	public void lerArquivoBoletos(String fileName) throws IOException {
		reset = true;
	    resetaVars();

	    try {
	        List<String> lines = Files.readAllLines(Paths.get(fileName));
	        /* O arquivo BoletosAtrasados.txt tem um cabeçalho na primeira linha(Codigo;Nomepagador;anoVcto;mesVcto;diaVcto;Vlrdocto)
	         * então está dando erro na hora de ler o ano, mês e dia
	         * Se precisar ler esse arquivo sem ter que tirar o cabeçalho, é preciso alterar o valor de i para 1
	         * */
	        for (int i = 1; i < lines.size(); i++) { //mude o valor de i para 1 para ler o BoletosAtrasados.txt sem tirar o cabeçalho
	        	String line = lines.get(i);
	            String[] fields = line.split(";"); //separando cada campo depois da ocorrência da ;
	            String codigo = fields[0];
	            String nomePagador = fields[1];
	            int anoVcto = Integer.parseInt(fields[2]);
	            int mesVcto = Integer.parseInt(fields[3]);
	            int diaVcto = Integer.parseInt(fields[4]);
	            
	            Calendar dataVencimento = Calendar.getInstance();
	            dataVencimento.set(Calendar.YEAR, anoVcto);
	            dataVencimento.set(Calendar.MONTH, (mesVcto-1)); //para dar o mês certo, tem que ser (mesVcto-1) vide classe Calendar.MONTH
	            dataVencimento.set(Calendar.DAY_OF_MONTH, diaVcto);

	            double valor = Double.parseDouble(fields[5]);

	            Boleto boleto = new Boleto(codigo, nomePagador, dataVencimento, valor);
	            listaBoletos.add(boleto);
	            vlrTotalBoletos += valor;
	        }
	        JOptionPane.showMessageDialog(null, "Arquivo lido com sucesso!\n" + fileName);
	    } catch (NumberFormatException e) {
	    	//lança um erro específico para formato ou valor incorreto no arquivo 
	        JOptionPane.showMessageDialog(null, "Erro ao ler o arquivo. Verifique se o formato CSV dele está correto. Erro lançado: " + e.getMessage()); 
	    }

	    
	}
	
	//2%multa e 1.2%juros por mes
	public void processarBoletos() {
		reset = false;
		resetaVars();
		for(Boleto boleto : listaBoletos) {
			double valor = boleto.getVlrDocto();
			boleto.setVlrMulta((valor * txMulta)); //usando o método set para guardar o valor da multa
			vlrTotalMultas += boleto.getVlrMulta(); //somando o valor total de multas
			int diasAtraso = boleto.getDiasAtraso(); //utilizando o método getDiasAtraso() da model Boleto
			somaDias += diasAtraso; //fazendo a soma de dias atrasados para depois calcular a média
			if(diasAtraso > maiorAtraso) maiorAtraso = diasAtraso; //verificação para definir qual foi o maior atraso
			if(diasAtraso < menorAtraso) menorAtraso = diasAtraso; //verificação para definir qual foi o menor atraso
			boleto.setVlrJuros(( valor * txJurosDia * diasAtraso )); //usando o método set para guardar o valor do juros
			vlrTotalJuros += boleto.getVlrJuros(); //somando o valor total de juros
			
		}
		mediaAtraso = somaDias / listaBoletos.size(); //cálculo da média de atraso em dias
		
		processado=true;
	}
	
	
	public void salvarArquivoBoletos(String fileName) throws IOException{
		StringBuilder sb = new StringBuilder(); //string builder para construir o arquivo de texto
		sb.append("Codigo;NomeDevedor;Vencimento;DiasAtraso;ValorDocto;VlrMulta;VlrJuros").append("\n"); //cabeçalho e depois pula a linha
		for(Boleto boleto : listaBoletos) {
			sb.append(boleto.toString()); //chama o método .toString() para construir a linha do boleto
			sb.append("\n"); //pula linha
		}
		
		try {
	        Files.write(Paths.get(fileName), sb.toString().getBytes());
	        JOptionPane.showMessageDialog(null, "Arquivo salvo com sucesso!\n" + fileName);
	    } catch (IOException e) {
	        JOptionPane.showMessageDialog(null, "Erro ao salvar o arquivo: " + e.getMessage());
	    }
		
	}
	
	//Metodo privado para voltar as variaveis para os valores iniciais
	private void resetaVars() {
		processado=false;
		
		maiorAtraso=0;
		menorAtraso=10000000;
		mediaAtraso=0;
		if(reset) { 
			//esse if foi implementado para só resetar o valor total dos boletos e limpar a lista de boletos quando o usuário clicar para abrir outro arquivo
			vlrTotalBoletos=0;
			listaBoletos.clear();
		}
		vlrTotalMultas=0;
		vlrTotalJuros=0;
		somaDias = 0;
	}
	
}
