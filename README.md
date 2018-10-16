#Task
На вход поступают пары (LocalDateTime, Callable). Нужно реализовать систему, которая будет выполнять Callable для
каждого пришедшего события в указанном LocalDateTime, либо как можно скорее в случае если система перегружена и не
успевает все выполнять (имеет беклог). Задачи должны выполняться в порядке согласно значению LocalDateTime либо в 
порядке прихода события для равных LocalDateTime. События могут приходить в произвольном порядке и добавление 
новых пар (LocalDateTime, Callable) может вызываться из разных потоков.

#Requirements
- Java 1.8+
- Maven 3.3.9+

#Build command
mvn clean install