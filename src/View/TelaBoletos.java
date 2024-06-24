package View;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.Controller;

public class TelaBoletos extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	//propriedade com o nome do arquivo de entrada
	private String fileName="";
	
	//Elementos da tela
	private JPanel pnlSuperior = new JPanel();
	private JPanel pnlInferior = new JPanel();
	private JButton btnOpenFile= new JButton("Abir arquivo");
	private JLabel lblFileName= new JLabel("");	
	private JTextArea txtSummary= new JTextArea("**** RESUMO ****",8,35);
	private JButton btnProcess= new JButton("Processar arquivo");
	private JButton btnSaveFile= new JButton("Salvar Arquivo de saida");

	private Controller control = new Controller();
	
	public TelaBoletos() {
		super("Processamento de Boletos em atraso");
		//Configura o frame
		this.setSize(600, 300);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Adiciona um listener aos botoes
		btnOpenFile.addActionListener(e-> {
			try {
				clickFileChooser(e);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnProcess.addActionListener(e-> clickProcessar(e));
		btnSaveFile.addActionListener(e-> {
			try {
				clickSalvar(e);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		pnlSuperior.add(btnOpenFile,BorderLayout.NORTH);
		pnlSuperior.add(lblFileName,BorderLayout.CENTER);
		this.add(pnlSuperior,BorderLayout.NORTH);
		
		txtSummary.setEditable(false);
		this.add(txtSummary,BorderLayout.CENTER);
		
		pnlInferior.setLayout(new GridLayout(1,2));
		pnlInferior.add(btnProcess);
		pnlInferior.add(btnSaveFile);
		this.add(pnlInferior,BorderLayout.SOUTH);
		
	}
	
	//Metodos privados da classe para adicionar eventos aos botoes
	private Object clickFileChooser(ActionEvent e) throws IOException {
		//Configuracao da janela JFileChooser
		JFileChooser filePick = new JFileChooser();
		FileNameExtensionFilter filtro= new FileNameExtensionFilter("Selecione o arquivo TXT","txt"); 
		filePick.setFileFilter(filtro);
		filePick.setDialogTitle("Selecionar o arquivo de boletos.");
		int retorno=filePick.showOpenDialog(null);
		//Testa se algum arquivo foi selecionado na janela JFileChooser
		if(retorno==JFileChooser.APPROVE_OPTION) {
			this.fileName =filePick.getSelectedFile().getAbsolutePath(); //Nome do arquivo com o caminho completo
				
			control.lerArquivoBoletos(fileName);  //Implementar esse metodo na classe Controle
			
			lblFileName.setText(fileName);
			txtSummary.setText(control.getSummary());
		
		}
		return null;
	}	
	
	private Object clickSalvar(ActionEvent e) throws IOException {
		if(control.isProcessado()) {	
			//O arquivo de saida sera salvo no mesmo local do arquivo de entrada
			//O nome do arquivo sera processado-ano-mes-dia--hhmmss.txt
			File inputFile= new File(this.fileName);
			Date hoje = new Date();
			final DateFormat df = new SimpleDateFormat("yyyy-MM-dd--hhmmss");
			String outputFileName= inputFile.getParent()+"\\processado-"+df.format(hoje)+".txt";
			File outputFile= new File(outputFileName);
			
			//Configuração da janela File Save
			JFileChooser fileSave = new JFileChooser();
			FileNameExtensionFilter filtro= new FileNameExtensionFilter("Arquivo TXT","txt"); 
			fileSave.setFileFilter(filtro);
			fileSave.setCurrentDirectory(outputFile);
			fileSave.setSelectedFile(outputFile);
			
			fileSave.setDialogTitle("Selecione onde salvar o arquivo de Saída.");
			int retorno=fileSave.showSaveDialog(this);
			if(retorno==JFileChooser.APPROVE_OPTION) {
				
				control.salvarArquivoBoletos(outputFileName); //Implementar esse metodo na classe Controle
				
				JOptionPane.showMessageDialog(null,"Arquivo\n"+outputFileName+"\nSalvo com sucesso!" );
			}	
			
		}else {
			JOptionPane.showInternalMessageDialog(null, "O Arquivo de entrada ainda não foi processado.\nÉ nessário processar antes de salvar o arquivo de saida.");
		}
		
		return null;
	}
	
	private Object clickProcessar(ActionEvent e) {
		if(fileName!="") {
			
			control.processarBoletos(); //Implementar esse metodo na classe Controle
			
			txtSummary.setText(control.getSummary());
			
		}else {
			JOptionPane.showInternalMessageDialog(null,"Não ha um arquivo para processar!");
		}
		return null;
		
	}
		
}
