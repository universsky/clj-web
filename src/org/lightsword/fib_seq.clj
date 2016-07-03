(ns org.lightsword.fib_seq)

(defn -main [& args]
  (prn "Hello, Fib!"))


;; 定义斐波那契数列
(defn fib []
  (defn fib-iter [a b]
    (lazy-seq (cons a (fib-iter b (+ a b)))))
  (fib-iter 0 1))

(println (take 10 (fib)))