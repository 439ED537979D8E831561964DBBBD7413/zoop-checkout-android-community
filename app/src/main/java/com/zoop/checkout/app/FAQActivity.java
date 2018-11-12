package com.zoop.checkout.app;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mainente on 05/05/15.
 */
public class FAQActivity extends ZCLMenuWithHomeButtonActivity {
    private ExpandableListView expandableListView;
    private List<String> listGroup;
    private HashMap<String, List<String>> listData;
    private HashMap<String, List<String>> listDataparent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        if (BuildConfig.APPLICATION_ID.equals("br.com.compufacil.checkout")) {
            buildCompufacilList();
        } else {
            buildList();
        }

        expandableListView = (ExpandableListView) findViewById(R.id.list_faq);

        expandableListView.setAdapter(new ExpandableAdapterFAQ(FAQActivity.this, listGroup,listData));

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Bundle transactionBundle = new Bundle();
                transactionBundle.putInt("selected_question", childPosition);
                FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("faq", transactionBundle);
                // Toast.makeText(C, "Group: " + groupPosition + "| Item: " + childPosition, Toast.LENGTH_SHORT).show();
                //if (groupPosition==
                return false;
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                }
                //        Toast.makeText(C, "Group (Expand): "+groupPosition, Toast.LENGTH_SHORT).show();

        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });
    }

    public void buildCompufacilList(){

        //ToDo: Passar tudo para o APISettings ASAP - ver Trello https://trello.com/c/kiwSqFaV
        //ToDo: Adicionar busca na FAQ - https://trello.com/c/kiwSqFaV
        //ToDo: Adicionar dados de variantes dinamicamente -https://trello.com/c/kiwSqFaV

        listGroup = new ArrayList<String>();

        listData = new HashMap<String, List<String>>();

        // GROUP
        listGroup.add("1. Como contactar o suporte?");
        listGroup.add("2. Quero me cadastrar, o que devo fazer?");
        listGroup.add("3. Preciso de um CNPJ (pessoa jurídica) para me cadastrar?");
        listGroup.add("4. Quais informações são solicitadas no momento do cadastro?");
        listGroup.add("5. É necessário fornecer alguma documentação adicional?" );
        listGroup.add("6. Qual e a diferença entre Conta Individual e Conta Empresarial?");
        listGroup.add("7. Quais são as taxas e preços?");
        listGroup.add("8. Qual é o valor mínimo para vendas pelo app?");
        listGroup.add("9. Como recebo o dinheiro das minhas vendas?");
        listGroup.add("10. Quais tipos de conta bancária a CompuFácil aceita?");
        listGroup.add("11. Quais bandeiras de cartões minha maquininha aceitará? Aceita débito?");
        listGroup.add("12. É preciso habilitar algum plano de dados no meu celular?");
        listGroup.add("13. Após a bateria carregada, deixo a maquininha ligada ou desligada?");


        int countList = 0;
        List<String> auxList;
        // CHILDREN
        auxList = new ArrayList<String>();
        //xx12
        auxList.add("O atendimento/ suporte preferencial do Checkout é por e-mail: suporte@compufacil.com.br. \nTelefone: (49) 3030 0400\n" +
                "WhatsApp: (49) 98435-9314\n");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("Para fins de cadastramento para utilização dos serviços, " +
                "basta preencher o formulário de credenciamento e adesão disponível online ou através do e-mail: credenciamento@compufacil.com.br" +
                "\n Você também poderá entrar em contato diretamente com um de nossos atendentes. Veja seção acima, \"Como contactar o suporte\"");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("Para ser um afiliado CompuFácil não há exigência quanto ao Número de Cadastro Nacional de Pessoas Jurídicas (CNPJ). O cadastro pode ser feito também para pessoas físicas, através do Número do Cadastro de Pessoas Físicas (CPF). \n");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("Abaixo estão listadas as informações exigidas:\n" +
                "Nome do responsável\n" +
                "Número do telefone e código de Discagem Direta a Distância (DDD)\n" +
                "CPF do responsável (para pessoa física)\n" +
                "CNPJ (para pessoa jurídica)\n" +
                "Tipo de atividade\n" +
                "Logo apos o cadastro inicial, você devera informar:\n" +
                "Endereço completo\n" +
                "Conta corrente atrelada ao CPF/CNPJ\n");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("No momento do credenciamento, são realizadas algumas verificações com o objetivo de garantir a corretude dos dados e visando a sua própria segurança. Estas verificações também seguem as diretrizes estabelecidas nas politicas de segurança exigidas pelo Banco Central e pelas bandeiras de cartões de crédito. \n" +
                "Caso existam dados incorretos ou incompletos, poderemos solicitar que você envie algumas informações adicionais, como por exemplo:\n" +
                "Cópia do documento de identificação (RG, carteira de motorista, etc.)\n" +
                "Cópia de um comprovante de residência em seu nome (conta de luz, gás, água, etc.)\n" +
                "Comprovante de atividade profissional (certificado profissional, nota de prestação de serviços, site, Facebook, material de divulgação etc.).\n");
        listData.put(listGroup.get(countList++), auxList);


        auxList = new ArrayList<String>();
        auxList.add("Se o seu negócio esta registrado como uma Sociedade Ltda., S.A. ou MEI (Micro Empreendedor Individual)," +
                " você deverá criar uma Conta Empresarial (pessoa jurídica). Caso contrário, crie uma Conta Individual (pessoa física). \n");
        listData.put(listGroup.get(countList++), auxList);


        auxList = new ArrayList<String>();
        auxList.add("Você paga apenas uma pequena taxa por transação realizada com sucesso. Sem letras miúdas, de acordo com o plano selecionado por você." +
                " Não há custo de adesão ou mensalidades nos planos. Além disso, você não ficará preso a nenhum contrato e pode cancelar a qualquer momento.\n\n" +
                "Plano Standard: \n" +
                "Débito: Prazo de 30 dias com taxa de 2,69%\n" +
                "Crédito à vista: Prazo de 30 dias com taxa de 3,99%.\n" +
                "Parcelado Lojista de 2 até 6 parcelas: Prazo de 30 dias com taxa de 4,39%.\n" +
                "Parcelado Lojista de 7 até 12 parcelas: Prazo de 30 dias com taxa de 4,69%.\n\n" +
                "Plano Pro: \n" +
                "Débito: Prazo de 1 dia com taxa de 2,99%\n" +
                "Crédito à vista: Prazo de 1 dia com taxa de 4,99%.\n" +
                "Parcelado Lojista de 2 até 12 parcelas: Prazo de 1 dia com taxa de 2,39% por parcela.\n\n" +
                "Não é necessário pagar outras taxas como:\n"+
                "- Mensalidade" +
                "- Aluguel da maquininha, " +
                "- Taxa de adesão, " +
                "- Taxa de cancelamento." +
                "");
        listData.put(listGroup.get(countList++), auxList);


        auxList = new ArrayList<String>();
        auxList.add("O valor mínimo para vendas à vista é de R$ 1,00 e para vendas parceladas o valor mínimo é de R$ 5,00 por parcela.");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("O crédito do valor liquido das transações serão automaticamente transferidos para a conta corrente ou conta poupança cadastrada no Portal de Serviços.\n" +
                "\n" +
                "As vendas realizadas no débito são pagas após dois dias úteis da transação (D+2). Já as transações efetuadas no crédito à vista serão pagas após trinta dias corridos da transação (D+30).\n" +
                "\n" +
                "Se a data prevista para o crédito do valor liquido da transação recair em feriado ou em dia de não funcionamento bancário na praça sede da CompuFácil ou na praça de compensação da conta para pagamento do afiliado, o pagamento será realizado no primeiro dia útil subsequente. \n");
        listData.put(listGroup.get(countList++), auxList);


        auxList = new ArrayList<String>();
        auxList.add("O afiliado poderá optar pelo depósito em conta corrente ou poupança. Você poderá cadastrar os dados bancários diretamente no seu Portal de Serviços. bancária deve ser vinculada ao CPF ou CNPJ do cadastro realizado na CompuFácil.");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("Com a maquininha de Chip & Senha CompuFácil, você poderá aceitar cartões de débito e crédito das maiores bandeiras do mundo: Visa, Visa Electron, " +
                "MasterCard e Maestro. Estamos incorporando novas bandeiras, de forma criteriosa e sem incorrer em custos para os nossos clientes." +
                " Você será avisado através dos nossos canais de comunicação. \n");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("Não necessariamente. Para o funcionamento da maquininha Chip&Senha CompuFácil é necessário que seu smartphone ou tablet esteja conectado à Internet, seja através de uma rede Wi-Fi, ou utilizando o serviço de dados via rede celular (2G, 3G, 4G, etc). \n" +
                "Para o serviço de dados via rede celular, você precisa de um cartão SIM (chip) habilitado com um plano de dados. Há diversos planos de dados disponíveis, e a maioria não exige um contrato. Entre em contato com sua operadora para obter informações sobre como obter um cartão SIM e um plano de dados. ");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("A maquininha de Chip&Senha CompuFácil pode ficar ligada, uma vez que ela ficará em modo de suspensão (idle). A bateria tem capacidade de duração de até 6 horas.");
        listData.put(listGroup.get(countList++), auxList);

    }

    public void buildList(){

        //ToDo: Passar tudo para o APISettings ASAP - ver Trello https://trello.com/c/kiwSqFaV
        //ToDo: Adicionar busca na FAQ - https://trello.com/c/kiwSqFaV
        //ToDo: Adicionar dados de variantes dinamicamente -https://trello.com/c/kiwSqFaV

        listGroup = new ArrayList<String>();

        listData = new HashMap<String, List<String>>();

        // GROUP
        listGroup.add("Como contactar o suporte?");
        listGroup.add("1. Quero me cadastrar, o que devo fazer?");
        listGroup.add("2. Preciso de um CNPJ (pessoa jurídica) para me cadastrar?");
        listGroup.add("3. Quais informações são solicitadas no momento do cadastro?");
        listGroup.add("4. É necessário fornecer alguma documentação adicional?" );
        listGroup.add("5. Qual e a diferença entre Conta Individual e Conta Empresarial?");
        listGroup.add("6. Quais são as taxas e preços?");
        listGroup.add("7. Qual é o valor mínimo para vendas pelo app?");
        listGroup.add("8. Como recebo o dinheiro das minhas vendas?");
        listGroup.add("9. Quais tipos de conta bancária a Zoop aceita?");
        listGroup.add("10. Como faço para cadastrar uma conta bancária no sistema?");
        listGroup.add("11. Quais bandeiras de cartões minha maquininha aceitará? Aceita débito?");
        listGroup.add("12. Quanto tempo minha maquininha segura de cartões leva para chegar?");
        listGroup.add("13. É preciso habilitar algum plano de dados no meu celular?");
        listGroup.add("14. Após a bateria carregada, deixo a maquininha ligada ou desligada?");


        int countList = 0;
        List<String> auxList;
        // CHILDREN
        auxList = new ArrayList<String>();
        //xx12
        auxList.add("O atendimento/ suporte preferencial do Zoop é por e-mail: suporte@pagzoop.com . \nTelefones:\nRio de Janeiro: (21) 3942-9667 (21 3942-ZOOP)\n" +
                "São Paulo: (11) 3042-4541\n" +
                "WhatsApp/ Celular: (21) 97590-8874\n" +
                "Skype: zoop_brasil\n");

        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("Para fins de cadastramento para utilização dos serviços, " +
                "basta preencher o formulário de credenciamento e adesão disponível online ou através do e-mail: credenciamento@pagzoop.com." +
                "\n Você também poderá entrar em contato diretamente com um de nossos atendentes. Veja seção acima, \"Como contactar o suporte\"");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("Para ser um afiliado Zoop não há exigência quanto ao Número de Cadastro Nacional de Pessoas Jurídicas (CNPJ). O cadastro pode ser feito também para pessoas físicas, através do Número do Cadastro de Pessoas Físicas (CPF). \n");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("Abaixo estão listadas as informações exigidas:\n" +
                        "Nome do responsável\n" +
                        "Número do telefone e código de Discagem Direta a Distância (DDD)\n" +
                        "CPF do responsável (para pessoa física)\n" +
                        "CNPJ (para pessoa jurídica)\n" +
                        "Tipo de atividade\n" +
                        "Logo apos o cadastro inicial, você devera informar:\n" +
                        "Endereço completo\n" +
                        "Conta corrente atrelada ao CPF/CNPJ\n");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("No momento do credenciamento, são realizadas algumas verificações com o objetivo de garantir a corretude dos dados e visando a sua própria segurança. Estas verificações também seguem as diretrizes estabelecidas nas politicas de segurança exigidas pelo Banco Central e pelas bandeiras de cartões de crédito. \n" +
                "Caso existam dados incorretos ou incompletos, poderemos solicitar que você envie algumas informações adicionais, como por exemplo:\n" +
                "Cópia do documento de identificação (RG, carteira de motorista, etc.)\n" +
                "Cópia de um comprovante de residência em seu nome (conta de luz, gás, água, etc.)\n" +
                "Comprovante de atividade profissional (certificado profissional, nota de prestação de serviços, site, Facebook, material de divulgação etc.).\n");
        listData.put(listGroup.get(countList++), auxList);


        auxList = new ArrayList<String>();
        auxList.add("Se o seu negócio esta registrado como uma Sociedade Ltda., S.A. ou MEI (Micro Empreendedor Individual)," +
                " você deverá criar uma Conta Empresarial (pessoa jurídica). Caso contrário, crie uma Conta Individual (pessoa física). \n");
        listData.put(listGroup.get(countList++), auxList);


        auxList = new ArrayList<String>();
        auxList.add("Você paga apenas uma pequena taxa por transação realizada com sucesso. Sem letras miúdas, de acordo com o plano selecionado por você." +
                " Não há custo de adesão ou mensalidades nos planos. Além disso, você não ficará preso a nenhum contrato e pode cancelar a qualquer momento.\n" +
                "Produto Crédito (com maquininha de Chip & Senha Zoop): \n" +
                "À vista / Emissor: Prazo de 30 dias com taxa de 3,99%.\n" +
                "Parcelado Lojista de 2 até 6 parcelas: Prazo de 30 dias com taxa de 4,39.\n" +
                "Parcelado Lojista de 6 até 12 parcelas: Prazo de 30 dias com taxa de 4,69.\n" +
                "Produto Débito (com maquininha Chip & Senha Zoop):\n" +
                "À vista: Prazo de 2 dias com taxa de 2,99. \n" +
                "Não é necessário pagar outras taxas como:\n"+
                "- Mensalidade" +
                "- Aluguel da maquininha, " +
                "- Taxa de adesão, " +
                "- Taxa de cancelamento." +
                "");
        listData.put(listGroup.get(countList++), auxList);


        auxList = new ArrayList<String>();
        auxList.add("O valor mínimo para vendas à vista é de R$ 1,00 e para vendas parceladas o valor mínimo é de R$ 5,00 por parcela.");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("O crédito do valor liquido das transações serão automaticamente transferidos para a conta corrente ou conta poupança cadastrada no Portal de Serviços.\n" +
                "\n" +
                "As vendas realizadas no débito são pagas após dois dias úteis da transação (D+2). Já as transações efetuadas no crédito à vista serão pagas após trinta dias corridos da transação (D+30).\n" +
                "\n" +
                "Se a data prevista para o crédito do valor liquido da transação recair em feriado ou em dia de não funcionamento bancário na praça sede da Zoop ou na praça de compensação da conta para pagamento do afiliado, o pagamento será realizado no primeiro dia útil subsequente. \n");
        listData.put(listGroup.get(countList++), auxList);


        auxList = new ArrayList<String>();
        auxList.add("O afiliado poderá optar pelo depósito em conta corrente ou poupança. Você poderá cadastrar os dados bancários diretamente no seu Portal de Serviços. bancária deve ser vinculada ao CPF ou CNPJ do cadastro realizado na Zoop.");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("Para que possamos depositar o dinheiro das suas vendas em cartão, é necessário que você informe corretamente os seus dados bancários. Para isso, basta seguir os passos abaixo:\n" +
                "Acesse o Portal de Serviços Zoop, utilizando o seu e-mail e senha\n" +
                "Clique na opção Dados Cadastrais, no menu lateral esquerdo\n" +
                "Cadastre seus dados bancários.\n" +
                "Você devera informar: Nome do Banco, Número da agência (sem o dígito), Número da conta (com o dígito) e Tipo da conta (corrente ou poupança). \n");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("Com a maquininha de Chip & Senha Zoop, você poderá aceitar cartões de débito e crédito das maiores bandeiras do mundo: Visa, Visa Electron, " +
                "MasterCard e Maestro. Estamos incorporando novas bandeiras, de forma criteriosa e sem incorrer em custos para os nossos clientes." +
                " Você será avisado através dos nossos canais de comunicação. \n");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("A maquininha de cartões de Chip&Senha Zoop são entregues por distribuidores ou pelo correio em até 15 dias úteis nas capitais ou até 20 dias úteis nas demais cidades do Brasil." +
                " Assim que a sua maquininha for enviada, você será avisado por e-mail, confirmando que ela está a caminho e que chegará em breve. ");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("Não necessariamente. Para o funcionamento da maquininha Chip&Senha Zoop é necessário que seu smartphone ou tablet esteja conectado à Internet, seja através de uma rede Wi-Fi, ou utilizando o serviço de dados via rede celular (2G, 3G, 4G, etc). \n" +
                "Para o serviço de dados via rede celular, você precisa de um cartão SIM (chip) habilitado com um plano de dados. Há diversos planos de dados disponíveis, e a maioria não exige um contrato. Entre em contato com sua operadora para obter informações sobre como obter um cartão SIM e um plano de dados. ");
        listData.put(listGroup.get(countList++), auxList);

        auxList = new ArrayList<String>();
        auxList.add("A maquininha de Chip&Senha Zoop pode ficar ligada, uma vez que ela ficará em modo de suspensão (idle). A bateria tem capacidade de duração de até 6 horas.");
        listData.put(listGroup.get(countList++), auxList);

    }
}