```bash
mvn test
```
Apenas executa testes unitários, não executa testes de integração ou seja testes com o sufixo IT. Para além disso não compila 
nem comprime o projeto.

---

```bash
mvn package
```
Compila o código do projeto, executa os testes unitários e para além disso gera um artefact .jar ou .war para a pasta target.

---

```bash
mvn package -DskipTests
```

Compila o código do projeto e gera o artefact .jar ou .war, porém não executa os testes unitários nem qualquer outro tipo de teste.

---

```bash
mvn failsafe:integration-test
```
Executa os testes de integração, e utiliza o plugin Failsafe que é fundamental para projetos que utilizam recursos externos por exemplo APIs, DBs etc...

---

```bash
mvn install
```

Compila o código do projeto, faz os testes unitários,, comprime o projeto e instala o artefact localmente. Permite que outros projetos 
usem esse artefact sem precisar recompilá-lo.

---