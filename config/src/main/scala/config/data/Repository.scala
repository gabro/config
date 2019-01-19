package config

trait Repository[F[_], A] {
  def read(id: Id[A]): F[Option[A]]
  def update(id: Id[A], value: A): F[Unit]
  def create[B](value: B): F[Id[A]]
  def delete(id: Id[A]): F[Unit]
}
