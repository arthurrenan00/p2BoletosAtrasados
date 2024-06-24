package model;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Boleto {
	private String codigo;
	private String pagador;
	private Calendar dataVenc;
	private double vlrDocto;
	private double vlrMulta;
	private double vlrJuros;
	
	//Construtores da Classe
	public Boleto() {
		
	}
	public Boleto(String codigo, String pagador, Calendar dataVenc, double vlrDocto) {		
		this.codigo = codigo;
		this.pagador = pagador;
		this.dataVenc = geraCalendar(dataVenc);
		this.vlrDocto = vlrDocto;
		this.vlrMulta=0;
		this.vlrJuros=0;
	}
	
	//Getters da classe, essa classe nao em setters	
	public String getCodigo() {
		return codigo;
	}
	
	public String getPagador() {
		return pagador;
	}
	
	public Calendar getDataVenc() {
		return dataVenc;
	}
	
	public double getVlrDocto() {
		return vlrDocto;
	}
	
		
	public double getVlrMulta() {
		return vlrMulta;
	}	
	
	public void setVlrMulta(double vlrMulta) {
		this.vlrMulta = vlrMulta;
	}
	
	
	public double getVlrJuros() {
		return vlrJuros;
	}
	
	public void setVlrJuros(double vlrJuros) {
		this.vlrJuros = vlrJuros;
	}
	public int getDiasAtraso() {
		Date hoje = new Date();
		Date dtVcto= dataVenc.getTime();
	
		int daysDiff= (int) TimeUnit.DAYS.convert(hoje.getTime()-dtVcto.getTime(), TimeUnit.MILLISECONDS);
		return  daysDiff;
	}
		
	//Metodos Privados da Classe
	private Calendar geraCalendar(Calendar data) {
		Calendar newCalendar = Calendar.getInstance();
		newCalendar.set(data.get(Calendar.YEAR) , data.get(Calendar.MONTH), data.get(Calendar.DAY_OF_MONTH));
		
		return newCalendar;
	}
	@Override
	public String toString() {
		final DateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
		DecimalFormat numberFormat= new DecimalFormat();
		numberFormat.applyPattern("0.00");
		
		String retorno = codigo + ";" + pagador+ ";" + dataFormat.format(dataVenc.getTime())+ ";" +getDiasAtraso()+";"+
				numberFormat.format(vlrDocto)+ ";" +numberFormat.format(vlrMulta)+ ";" +numberFormat.format(vlrJuros); 
		
		
		return  retorno;
	}
}
