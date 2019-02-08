# zoop-checkout-android-community

Se você é desenvolvedor (ou tem acesso à uma equipe de desenvolvimento) e deseja criar seu próprio aplicativo para cobranças com mPOS, você pode utilizar este código como base para sua solução.

Este repositório disponibiliza o código fonte de um aplicativo Android baseado no Zoop Checkout, portanto, apresenta conteúdo como textos, cores e imagens relacionados à esta solução. Não hesite e customize todos estes pontos para sua identidade visual!* 

Você pode fazer forks do projeto ou simplesmente criar seu próprio repositório com o conteúdo alterado. Como esta é uma solução open-source, queremos o apoio da comunidade! Correções e PRs são super bem vindos!**

Por fim, o projeto utiliza nosso SDK de pagamentos Android. Caso note algum problema com seu funcionamento, entre em contato com nosso suporte através do e-mail: suporte@zoop.co

Siga em frente e divirta-se!

——————————————
Instruções para customização:

1. As imagens referentes ao seu aplicativo podem ser alterar na pasta drawable (src > checkout > res > drawable)
2. O nome do apk a ser gerado pode ser alterado por meio do arquivo product_name (src > checkout > res > values > product_name)
3. O estilo pode ser alterado pelo arquivo style (src > checkout > res > values > styles)
4. Por fim, deverá ser alterado o arquivo application_configuration (src > checkout > app > application_configuration). Nele há a linha "public static final String CHECKOUT_API_PRODUCTION_PUBLIC_KEY = '' ". Insira a CKT (chave de identificação do seu marketplace. Tal chave deve ser solicitada ao suporte da zoop)
———————————————
*Não nos responsabilizamos pelo conteúdo apresentado nos aplicativos gerados através deste repositório.
** Como uma solução open-source, a comunidade é a responsável pela qualidade da solução. Correções e alterações apenas serão realizados por contribuidores e não por integrantes da Zoop.
